package com.cg.loanemicalculator.service;

import com.cg.loanemicalculator.dto.*;
import com.cg.loanemicalculator.exception.ResourceNotFound;
import com.cg.loanemicalculator.model.AmortizationSchedule;
import com.cg.loanemicalculator.model.Loan;
import com.cg.loanemicalculator.model.Payment;
import com.cg.loanemicalculator.model.PaymentStatus;
import com.cg.loanemicalculator.repository.AmortizationScheduleRepository;
import com.cg.loanemicalculator.repository.LoanRepository;
import com.cg.loanemicalculator.repository.PaymentRepository;
import com.cg.loanemicalculator.security.ResourceOwnershipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final LoanRepository loanRepository;
    private final ResourceOwnershipValidator resourceOwnershipValidator;
    private final AmortizationScheduleRepository amortizationScheduleRepository;

    @Override
    public PaymentDTO addPayment(Integer loanId, Integer userId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(()->new IllegalArgumentException("Invalid loan"));
        resourceOwnershipValidator.validateLoanOwnership(loan, userId);

        if (loan.getStatus().equals(Loan.LoanStatus.CLOSED)){
            throw new ResourceNotFound("This Loan has already been closed");
        }

        Payment payment = new Payment();

        AmortizationSchedule amortizationSchedule = null;
        AmortizationSchedule lastAmortizationSchedule = null;
        List<AmortizationSchedule> amortizationScheduleList = amortizationScheduleRepository.findByLoanIdOrderByIdAsc(loanId);
        for (AmortizationSchedule schedule : amortizationScheduleList) {
            if (!schedule.isRepaymentDone()){
                schedule.setRepaymentDone(true);
                amortizationSchedule = schedule;
                break;
            }
            lastAmortizationSchedule = schedule;
        }
        LocalDate lastDate;
        if (amortizationSchedule == null){
            throw new ResourceNotFound("No Amortization Schedule found");
        }
        if (lastAmortizationSchedule == null){
            lastDate = amortizationSchedule.getPaymentDate().minusMonths(1);
        }else{
            lastDate = lastAmortizationSchedule.getPaymentDate();
        }
        payment.setAmount(amortizationSchedule.getEmi());
        payment.setLoan(loan);
        payment.setDate(LocalDate.now());

        payment.setStatus(getPaymentStatus(
                payment.getDate(),
                lastDate,
                amortizationSchedule.getPaymentDate()
        ));

        if (payment.getStatus().equals(PaymentStatus.EARLY)){
            throw new IllegalArgumentException("Last eligible payment has already been completed");
        }

        if (amortizationSchedule.getMonth().equals(loan.getTenureMonths())){
            loan.setStatus(Loan.LoanStatus.CLOSED);
        }
        loan.setOutstandingPrincipal(loan.getOutstandingPrincipal().subtract(amortizationSchedule.getPrincipalComponent()));
        loanRepository.save(loan);

        paymentRepository.save(payment);
        amortizationScheduleRepository.save(amortizationSchedule);
        return convertToDTO(payment);
    }

    @Override
    public List<PaymentDTO> getPayments(Integer loanId, Integer userId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(()->new IllegalArgumentException("Invalid loan"));
        resourceOwnershipValidator.validateLoanOwnership(loan, userId);
        return paymentRepository.getPaymentsByLoan(loan).stream().map(this:: convertToDTO).toList();
    }

    private static PaymentStatus getPaymentStatus(LocalDate paymentDate, LocalDate startDate,  LocalDate expectedDueDate) {
        if (paymentDate.isBefore(expectedDueDate) && paymentDate.isAfter(startDate)) {
            return PaymentStatus.ON_TIME;
        } else if (paymentDate.isBefore(startDate)) {
            return PaymentStatus.EARLY;
        } else {
            return PaymentStatus.DELAYED;
        }
    }

    private PaymentDTO convertToDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .loanId(payment.getLoan().getId())
                .date(payment.getDate())
                .status(payment.getStatus())
                .build();
    }
}

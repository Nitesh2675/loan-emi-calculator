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

        AmortizationSchedule amortizationSchedule = new AmortizationSchedule();
        List<AmortizationSchedule> amortizationScheduleList = amortizationScheduleRepository.findByLoanIdOrderByIdAsc(loanId);
        System.out.println(amortizationScheduleList);
        for (AmortizationSchedule schedule : amortizationScheduleList) {
            if (!schedule.isRepaymentDone()){
                schedule.setRepaymentDone(true);
                amortizationSchedule = schedule;
                break;
            }
        }

        payment.setAmount(amortizationSchedule.getEmi());
        payment.setLoan(loan);
        payment.setDate(LocalDate.now());

        payment.setStatus(getPaymentStatus(
                loan.getStartDate(),
                loan.getTenureMonths(),
                payment.getDate()
        ));

        if (amortizationSchedule.getMonth().equals(loan.getTenureMonths())){
            loan.setStatus(Loan.LoanStatus.CLOSED);
            loanRepository.save(loan);
        }

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

    private static PaymentStatus getPaymentStatus(LocalDate startDate, int tenureMonths, LocalDate paymentDate) {
        if (paymentDate.isBefore(startDate)) {
            return PaymentStatus.INVALID_DATE;
        }

        int yearDiff = paymentDate.getYear() - startDate.getYear();
        int monthDiff = paymentDate.getMonthValue() - startDate.getMonthValue();
        int totalMonthsPassed = (yearDiff * 12) + monthDiff;

        if (totalMonthsPassed >= tenureMonths) {
            totalMonthsPassed = tenureMonths - 1; // prevent overshooting the loan
        }

        LocalDate expectedDueDate = startDate.plusMonths(totalMonthsPassed);

        if (paymentDate.isEqual(expectedDueDate)) {
            return PaymentStatus.ON_TIME;
        } else if (paymentDate.isBefore(expectedDueDate)) {
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

package com.cg.loanemicalculator.service;

import com.cg.loanemicalculator.dto.AmortizationScheduleEntryDto;
import com.cg.loanemicalculator.dto.PrepaymentDTO;
import com.cg.loanemicalculator.dto.PrepaymentRequestDTO;
import com.cg.loanemicalculator.exception.ResourceNotFound;
import com.cg.loanemicalculator.model.AmortizationSchedule;
import com.cg.loanemicalculator.model.Loan;
import com.cg.loanemicalculator.model.Prepayment;
import com.cg.loanemicalculator.repository.AmortizationScheduleRepository;
import com.cg.loanemicalculator.repository.LoanRepository;
import com.cg.loanemicalculator.repository.PrepaymentRepository;
import com.cg.loanemicalculator.security.ResourceOwnershipValidator;
import com.cg.loanemicalculator.util.ScheduleGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrepaymentServiceImpl implements PrepaymentService {

    private final LoanRepository loanRepository;
    private final PrepaymentRepository prepaymentRepository;
    private final ResourceOwnershipValidator resourceOwnershipValidator;
    private final AmortizationScheduleRepository amortizationScheduleRepository;

    @Override
    public PrepaymentDTO createPrepayment(Integer loanId, PrepaymentRequestDTO prepaymentRequestDTO, Integer userId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new ResourceNotFound("Loan not found"));
        if (prepaymentRequestDTO.getAmount().compareTo(loan.getOutstandingPrincipal()) > 0){
            throw new IllegalArgumentException("Prepayment amount is greater than outstanding principal");
        }
        resourceOwnershipValidator.validateLoanOwnership(loan, userId);
        Prepayment prepayment = Prepayment.builder()
                .amount(prepaymentRequestDTO.getAmount())
                .paymentDate(LocalDate.now())
                .prepaymentType(prepaymentRequestDTO.getPrepaymentType())
                .build();
        prepayment.setLoan(loan);
        loan.setOutstandingPrincipal(loan.getOutstandingPrincipal().subtract(prepayment.getAmount()));

        List<AmortizationSchedule> scheduleList = amortizationScheduleRepository.findByLoanIdOrderByIdAsc(loanId);
        int lastPaidMonth = 0;

        for (AmortizationSchedule s : scheduleList) {
            if (s.isRepaymentDone()) {
                lastPaidMonth = s.getMonth();
            }
        }

        List<AmortizationSchedule> unpaid = scheduleList.stream()
                .filter(s -> !s.isRepaymentDone())
                .toList();
        amortizationScheduleRepository.deleteAll(unpaid);

        //update amortization
        List<AmortizationScheduleEntryDto> scheduleDtoList =  ScheduleGeneratorUtil.recalculateFromMonth(loan, lastPaidMonth + 1, unpaid.getFirst().getPaymentDate(), prepayment.getPrepaymentType());

        List<AmortizationSchedule> scheduleEntities = scheduleDtoList.stream()
                .map(dto -> AmortizationSchedule.builder()
                        .loanId(loanId)
                        .month(dto.getMonth())
                        .paymentDate(dto.getPaymentDate())
                        .beginningBalance(dto.getBeginningBalance())
                        .emi(dto.getEmi())
                        .principalComponent(dto.getPrincipalComponent())
                        .interestComponent(dto.getInterestComponent())
                        .endingBalance(dto.getEndingBalance())
                        .repaymentDone(false)
                        .build())
                .collect(Collectors.toList());

        amortizationScheduleRepository.saveAll(scheduleEntities);

        loanRepository.save(loan);
        prepaymentRepository.save(prepayment);
        return covertToPrepaymentDTO(prepayment);
    }

    @Override
    public List<PrepaymentDTO> getPrepayments(Integer loanId, Integer userId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new ResourceNotFound("Loan not found"));
        resourceOwnershipValidator.validateLoanOwnership(loan, userId);
        return prepaymentRepository.findPrepaymentsByLoan(loan);
    }

    private PrepaymentDTO covertToPrepaymentDTO(Prepayment prepayment) {
        return PrepaymentDTO.builder()
                .id(prepayment.getId())
                .loanId(prepayment.getLoan().getId())
                .paymentDate(prepayment.getPaymentDate())
                .amount(prepayment.getAmount())
                .prepaymentType(prepayment.getPrepaymentType())
                .build();
    }
}

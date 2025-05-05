package com.cg.loanemicalculator.service;

import com.cg.loanemicalculator.dto.AmortizationScheduleEntryDto;
import com.cg.loanemicalculator.model.AmortizationSchedule;
import com.cg.loanemicalculator.model.Loan;
import com.cg.loanemicalculator.repository.AmortizationScheduleRepository;
import com.cg.loanemicalculator.repository.LoanRepository;
import com.cg.loanemicalculator.util.PdfExportUtil;
import com.cg.loanemicalculator.util.ScheduleGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.cg.loanemicalculator.security.ResourceOwnershipValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AmortizationScheduleServiceImpl implements AmortizationScheduleService {

    private final LoanRepository loanRepository;
    private final AmortizationScheduleRepository scheduleRepository;
    private final ResourceOwnershipValidator authz;


    @Override
    public List<AmortizationScheduleEntryDto> generateSchedule(Integer loanId,  Integer userId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found with ID: " + loanId));
        authz.validateLoanOwnership(loan, userId);
        return ScheduleGeneratorUtil.generateSchedule(loan);
    }

    @Override
    public byte[] exportSchedule(Integer loanId, String format,  Integer userId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found with ID: " + loanId));
        authz.validateLoanOwnership(loan, userId);
        List<AmortizationSchedule> schedule = scheduleRepository.findByLoanIdOrderByIdAsc(loanId);

        if ("pdf".equalsIgnoreCase(format)) {
            return PdfExportUtil.exportToPdf(schedule);
        } else {
            throw new IllegalArgumentException("Invalid format: " + format);
        }
    }

    @Override
    public void generateAndSaveSchedule(Integer loanId,  Integer userId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found with ID: " + loanId));
        authz.validateLoanOwnership(loan, userId);

        // Check if schedule already exists for this loan
        boolean alreadyExists = scheduleRepository.existsByLoanId(loanId);
        if (alreadyExists) {
            throw new IllegalStateException("Amortization schedule already exists for Loan ID: " + loanId);
        }

        List<AmortizationScheduleEntryDto> scheduleDtoList = ScheduleGeneratorUtil.generateSchedule(loan);

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

        scheduleRepository.saveAll(scheduleEntities);
    }

}

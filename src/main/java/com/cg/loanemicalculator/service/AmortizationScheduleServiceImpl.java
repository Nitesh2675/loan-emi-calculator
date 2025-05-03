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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AmortizationScheduleServiceImpl implements AmortizationScheduleService {

    private final LoanRepository loanRepository;
    private final AmortizationScheduleRepository scheduleRepository;

    @Override
    public List<AmortizationScheduleEntryDto> generateSchedule(Integer loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found with ID: " + loanId));
        return ScheduleGeneratorUtil.generateSchedule(loan);
    }

    @Override
    public byte[] exportSchedule(Integer loanId, String format) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found with ID: " + loanId));
        List<AmortizationScheduleEntryDto> schedule = ScheduleGeneratorUtil.generateSchedule(loan);

        if ("pdf".equalsIgnoreCase(format)) {
            return PdfExportUtil.exportToPdf(schedule);
        } else {
            throw new IllegalArgumentException("Invalid format: " + format);
        }
    }

    @Override
    public void generateAndSaveSchedule(Integer loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found with ID: " + loanId));

        // Check if schedule already exists for the loan (prevents duplicate entries)
        if (!scheduleRepository.findByLoanId(loanId).isEmpty()) {
            throw new IllegalArgumentException("Amortization schedule already exists for this loan.");
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
                        .repaymentDone(false) // Default value, indicating payment not yet made
                        .build())
                .collect(Collectors.toList());

        // Save all generated schedules to the DB
        scheduleRepository.saveAll(scheduleEntities);
    }
}

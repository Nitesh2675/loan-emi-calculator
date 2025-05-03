package com.cg.loanemicalculator.service;

import com.cg.loanemicalculator.dto.AmortizationScheduleEntryDto;
import com.cg.loanemicalculator.model.Loan;
import com.cg.loanemicalculator.repository.LoanRepository;
import com.cg.loanemicalculator.service.AmortizationScheduleService;
import com.cg.loanemicalculator.util.PdfExportUtil;
import com.cg.loanemicalculator.util.ScheduleGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AmortizationScheduleServiceImpl implements AmortizationScheduleService {

    private final LoanRepository loanRepository;

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

}


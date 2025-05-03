package com.cg.loanemicalculator.service;

import com.cg.loanemicalculator.dto.AmortizationScheduleEntryDto;

import java.util.List;

public interface AmortizationScheduleService {
    void generateAndSaveSchedule(Integer loanId);
    List<AmortizationScheduleEntryDto> generateSchedule(Integer loanId);
    byte[] exportSchedule(Integer loanId, String format); // format = "pdf" or "excel"
}


package com.cg.loanemicalculator.service;

import com.cg.loanemicalculator.dto.AmortizationScheduleEntryDto;

import java.util.List;

public interface AmortizationScheduleService {
    void generateAndSaveSchedule(Integer loanId, Integer userId);
    List<AmortizationScheduleEntryDto> generateSchedule(Integer loanId, Integer userId);
    byte[] exportSchedule(Integer loanId, String format, Integer userId);
}

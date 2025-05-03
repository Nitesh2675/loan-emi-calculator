package com.cg.loanemicalculator.controller;


import com.cg.loanemicalculator.dto.AmortizationScheduleEntryDto;
import com.cg.loanemicalculator.service.AmortizationScheduleService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/loans/{loanId}/schedule")
@RequiredArgsConstructor
public class AmortizationScheduleController {

    private final AmortizationScheduleService scheduleService;

    @GetMapping
    public List<AmortizationScheduleEntryDto> getAmortizationSchedule(@PathVariable Integer loanId) {
        return scheduleService.generateSchedule(loanId);
    }

    @GetMapping("/export")
    public void exportSchedule(@PathVariable Integer loanId,
                               @RequestParam(defaultValue = "pdf") String format,
                               HttpServletResponse response) throws IOException {
        byte[] data = scheduleService.exportSchedule(loanId, format.toLowerCase());

        String contentType = format.equalsIgnoreCase("excel") ?
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
                "application/pdf";

        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment; filename=amortization_schedule." + format);
        response.getOutputStream().write(data);
    }
}


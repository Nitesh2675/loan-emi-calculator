package com.cg.loanemicalculator.controller;

import com.cg.loanemicalculator.dto.AmortizationScheduleEntryDto;
import com.cg.loanemicalculator.service.AmortizationScheduleService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

        String contentType = "application/pdf";
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment; filename=amortization_schedule." + format);
        response.getOutputStream().write(data);
    }

    // ✅ New Endpoint to Save Schedule to Database
    @PostMapping("/save")
    public ResponseEntity<String> generateAndSaveSchedule(@PathVariable Integer loanId) {
        scheduleService.generateAndSaveSchedule(loanId);
        return ResponseEntity.ok("Amortization schedule saved to the database for loan ID: " + loanId);
    }
}

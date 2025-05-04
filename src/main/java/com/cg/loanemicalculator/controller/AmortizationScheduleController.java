package com.cg.loanemicalculator.controller;

import com.cg.loanemicalculator.dto.AmortizationScheduleEntryDto;
import com.cg.loanemicalculator.service.AmortizationScheduleService;
import jakarta.servlet.http.HttpServletRequest;
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
    public List<AmortizationScheduleEntryDto> getAmortizationSchedule(
            @PathVariable Integer loanId,
            HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        return scheduleService.generateSchedule(loanId, userId);
    }

    @GetMapping("/export")
    public void exportSchedule(@PathVariable Integer loanId,
                               @RequestParam(defaultValue = "pdf") String format,
                               HttpServletRequest request,
                               HttpServletResponse response) throws IOException {
        Integer userId = (Integer) request.getAttribute("userId");
        byte[] data = scheduleService.exportSchedule(loanId, format.toLowerCase(), userId);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=amortization_schedule." + format);
        response.getOutputStream().write(data);
    }

    @PostMapping("/save")
    public ResponseEntity<String> generateAndSaveSchedule(
            @PathVariable Integer loanId,
            HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        scheduleService.generateAndSaveSchedule(loanId, userId);
        return ResponseEntity.ok("Amortization schedule saved to the database for loan ID: " + loanId);
    }

}

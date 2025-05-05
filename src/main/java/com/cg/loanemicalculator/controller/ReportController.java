package com.cg.loanemicalculator.controller;

import com.cg.loanemicalculator.dto.LoanSummaryDto;
import com.cg.loanemicalculator.service.LoanSummaryService;
import com.cg.loanemicalculator.util.ReportGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final LoanSummaryService summarySvc;
    private final ReportGenerator    reportGenerator;

    @GetMapping("/loan-summary")
    public ResponseEntity<byte[]> downloadLoanSummary(
            @AuthenticationPrincipal com.cg.loanemicalculator.model.User user
    ) {
        // now user.getId() is never null
        Integer userId = user.getId();

        LoanSummaryDto dto = summarySvc.getLoanSummaryForUser(user.getId().longValue());
        byte[] pdf        = reportGenerator.generateLoanSummaryPdf(dto);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=loan-summary.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}

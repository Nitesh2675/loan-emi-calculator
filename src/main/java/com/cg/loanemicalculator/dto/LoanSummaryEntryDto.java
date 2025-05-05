package com.cg.loanemicalculator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class LoanSummaryEntryDto {
    private Integer   loanId;
    private BigDecimal principalAmount;
    private BigDecimal outstandingBalance;
    private BigDecimal totalInterestPaid;
    private LocalDate  startDate;
    private LocalDate  endDate;
    private BigDecimal emiAmount;
    private BigDecimal totalPrepayments;
}

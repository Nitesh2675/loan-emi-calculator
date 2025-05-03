package com.cg.loanemicalculator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AmortizationScheduleEntryDto {
    private int month;
    private LocalDate paymentDate;
    private BigDecimal beginningBalance;
    private BigDecimal emi;
    private BigDecimal principalComponent;
    private BigDecimal interestComponent;
    private BigDecimal endingBalance;

    // ✅ New field
    private boolean repaymentDone; // false = ✗, true = ✓
}


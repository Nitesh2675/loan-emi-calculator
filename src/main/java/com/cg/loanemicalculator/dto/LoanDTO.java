package com.cg.loanemicalculator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanDTO {
    private Integer id;
    private Integer userId;
    private String name;
    private BigDecimal principalAmount;
    private BigDecimal annualInterestRate;
    private Integer tenureMonths;
    private BigDecimal emi;
    private BigDecimal totalInterest;
    private BigDecimal totalPayment;
    private LocalDate startDate;
    private String status;
}

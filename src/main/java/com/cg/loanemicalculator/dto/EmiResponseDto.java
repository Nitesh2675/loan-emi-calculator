package com.cg.loanemicalculator.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class EmiResponseDto {
    private BigDecimal emi;
    private BigDecimal totalPayment;
    private BigDecimal totalInterest;
}

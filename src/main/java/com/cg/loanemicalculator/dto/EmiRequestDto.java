package com.cg.loanemicalculator.dto;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Builder
@Data
public class EmiRequestDto {
    @NotNull @Min(1)
    private BigDecimal principal;

    @NotNull @Min(0)
    private BigDecimal annualRatePct;

    @NotNull @Min(1)
    private Integer tenureMonths;
}

package com.cg.loanemicalculator.dto;

import com.cg.loanemicalculator.model.PrepaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrepaymentRequestDTO {
    private BigDecimal amount;
    private PrepaymentType prepaymentType;
}

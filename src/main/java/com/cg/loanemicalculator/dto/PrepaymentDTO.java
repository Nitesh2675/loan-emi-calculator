package com.cg.loanemicalculator.dto;

import com.cg.loanemicalculator.model.PrepaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrepaymentDTO {
    private Integer id;
    private Integer loanId;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private PrepaymentType prepaymentType;
}

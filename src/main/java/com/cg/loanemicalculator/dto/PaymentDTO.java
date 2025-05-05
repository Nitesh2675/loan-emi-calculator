package com.cg.loanemicalculator.dto;

import com.cg.loanemicalculator.model.PaymentStatus;
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
public class PaymentDTO {
    private Integer id;
    private Integer loanId;
    private LocalDate date;
    private BigDecimal amount;
    private PaymentStatus status;
}

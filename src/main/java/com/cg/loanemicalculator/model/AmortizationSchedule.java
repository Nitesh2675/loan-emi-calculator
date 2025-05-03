package com.cg.loanemicalculator.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "amortization_schedule")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmortizationSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer loanId;

    private Integer month;

    private LocalDate paymentDate;

    private BigDecimal beginningBalance;

    private BigDecimal emi;

    private BigDecimal principalComponent;

    private BigDecimal interestComponent;

    private BigDecimal endingBalance;

    @Column(name = "repayment_done")
    private boolean repaymentDone = false;
}

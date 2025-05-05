package com.cg.loanemicalculator.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @ManyToOne
    @JoinColumn(name = "loan_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Loan loan;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Column(nullable = false)
    private BigDecimal beginningBalance;

    @Column(nullable = false)
    private BigDecimal emi;

    @Column(nullable = false)
    private BigDecimal principalComponent;

    @Column(nullable = false)
    private BigDecimal interestComponent;

    @Column(nullable = false)
    private BigDecimal endingBalance;

    @Column(name = "repayment_done", nullable = false)
    private boolean repaymentDone = false;
}

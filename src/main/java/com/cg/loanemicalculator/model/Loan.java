package com.cg.loanemicalculator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal principalAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal annualInterestRate;

    @Column(nullable = false)
    private Integer tenureMonths;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal emi;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    public enum LoanStatus {
        ACTIVE, CLOSED
    }
}

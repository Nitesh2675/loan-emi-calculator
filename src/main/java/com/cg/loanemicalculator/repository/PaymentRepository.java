package com.cg.loanemicalculator.repository;

import com.cg.loanemicalculator.dto.PaymentDTO;
import com.cg.loanemicalculator.model.Loan;
import com.cg.loanemicalculator.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> getPaymentsByLoan(Loan loan);
}

package com.cg.loanemicalculator.repository;

import com.cg.loanemicalculator.dto.PrepaymentDTO;
import com.cg.loanemicalculator.model.Loan;
import com.cg.loanemicalculator.model.Prepayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrepaymentRepository extends JpaRepository<Prepayment, Integer> {
    List<Prepayment> findByLoan(Loan loan);
}

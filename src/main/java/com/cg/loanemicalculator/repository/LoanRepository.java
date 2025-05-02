package com.cg.loanemicalculator.repository;

import com.cg.loanemicalculator.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Integer> {
    List<Loan> findByUserId(Integer userId);
    Loan findByName(String name);
}

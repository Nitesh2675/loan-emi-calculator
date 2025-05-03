package com.cg.loanemicalculator.service;

import com.cg.loanemicalculator.dto.LoanDTO;
import com.cg.loanemicalculator.dto.LoanRequestDTO;
import com.cg.loanemicalculator.model.Loan;

import java.util.List;

public interface LoanService {
    LoanDTO createLoan(LoanRequestDTO loanRequestDTO);
    LoanDTO updateLoan(Integer loanId, LoanRequestDTO loanRequestDTO);
    void deleteLoan(Integer loanId, Integer userId);
    List<LoanDTO> getUserLoans(Integer userId);
    LoanDTO toggleLoanStatus(Integer loanId, Loan.LoanStatus newStatus, Integer userId);
    void evaluateAndMarkLoans();
}

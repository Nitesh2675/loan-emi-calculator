package com.cg.loanemicalculator.service;

import com.cg.loanemicalculator.dto.LoanDTO;
import com.cg.loanemicalculator.dto.LoanRequestDTO;

import java.util.List;

public interface LoanService {
    LoanDTO createLoan(LoanRequestDTO loanRequestDTO);
    LoanDTO updateLoan(Integer loanId, LoanRequestDTO loanRequestDTO);
    void deleteLoan(Integer loanId);
    List<LoanDTO> getUserLoans(Integer userId);
}

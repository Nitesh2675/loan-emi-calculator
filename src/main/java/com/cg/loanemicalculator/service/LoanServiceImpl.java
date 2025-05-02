package com.cg.loanemicalculator.service;

import com.cg.loanemicalculator.dto.LoanDTO;
import com.cg.loanemicalculator.dto.LoanRequestDTO;
import com.cg.loanemicalculator.exception.ResourceNotFound;
import com.cg.loanemicalculator.model.Loan;
import com.cg.loanemicalculator.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;

    @Override
    public LoanDTO createLoan(LoanRequestDTO loanRequestDTO) {
        Integer userId = loanRequestDTO.getUserId();
        log.info("Creating a new loan for user {}", userId);

        //calculated from emi calculator
        BigDecimal emi = new BigDecimal(0);
        BigDecimal totalInterest = new BigDecimal(0);
        BigDecimal totalPayment = new BigDecimal(0);

        Loan loan = Loan.builder()
                .name(loanRequestDTO.getName())
                .principalAmount(loanRequestDTO.getPrincipalAmount())
                .annualInterestRate(loanRequestDTO.getAnnualInterestRate())
                .tenureMonths(loanRequestDTO.getTenureMonths())
                .emi(emi)
                .startDate(loanRequestDTO.getStartDate())
                .status(Loan.LoanStatus.ACTIVE)
                .totalInterest(totalInterest)
                .totalPayment(totalPayment)
                .userId(userId)
                .build();

        loan  = loanRepository.save(loan);
        log.info("Created a new loan for user {}", userId);
        return convertToLoanDTO(loan);
    }

    @Override
    public LoanDTO updateLoan(Integer loanId, LoanRequestDTO loanRequestDTO) {
        log.info("Updating loan with id {}", loanId);
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFound("Loan not found with id " + loanId + " for loan creation"));

        loan.setName(loanRequestDTO.getName());
        loan.setPrincipalAmount(loanRequestDTO.getPrincipalAmount());
        loan.setAnnualInterestRate(loanRequestDTO.getAnnualInterestRate());
        loan.setTenureMonths(loanRequestDTO.getTenureMonths());
        loan.setStartDate(loanRequestDTO.getStartDate());

        loanRepository.save(loan);
        log.info("Updated loan with id {}", loanId);

        return convertToLoanDTO(loan);
    }

    @Override
    public void deleteLoan(Integer loanId) {
        log.info("Deleting loan with id {}", loanId);
        loanRepository.deleteById(loanId);
        log.info("Deleted loan with id {}", loanId);
    }

    @Override
    public List<LoanDTO> getUserLoans(Integer userId) {
        log.info("Retrieving user loans for user {}", userId);

        List<LoanDTO> loanList = loanRepository.findByUserId(userId).stream()
                .map(this::convertToLoanDTO).toList();

        log.info("Retrieved user loans for user {}", userId);
        return loanList;
    }

    private LoanDTO convertToLoanDTO(Loan loan) {
        return LoanDTO.builder()
                .id(loan.getId())
                .name(loan.getName())
                .principalAmount(loan.getPrincipalAmount())
                .annualInterestRate(loan.getAnnualInterestRate())
                .tenureMonths(loan.getTenureMonths())
                .startDate(loan.getStartDate())
                .emi(loan.getEmi())
                .userId(loan.getUserId())
                .totalPayment(loan.getTotalPayment())
                .totalInterest(loan.getTotalInterest())
                .status(loan.getStatus().name())
                .build();
    }
}

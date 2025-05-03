package com.cg.loanemicalculator.service;

import com.cg.loanemicalculator.dto.EmiRequestDto;
import com.cg.loanemicalculator.dto.EmiResponseDto;
import com.cg.loanemicalculator.dto.LoanDTO;
import com.cg.loanemicalculator.dto.LoanRequestDTO;
import com.cg.loanemicalculator.exception.ResourceNotFound;
import com.cg.loanemicalculator.model.Loan;
import com.cg.loanemicalculator.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final EmiService emiService;

    @Override
    public LoanDTO createLoan(LoanRequestDTO loanRequestDTO) {
        Integer userId = loanRequestDTO.getUserId();
        log.info("Creating a new loan for user {}", userId);

        EmiRequestDto emiReq = EmiRequestDto.builder()
                .principal(loanRequestDTO.getPrincipalAmount())
                .annualRatePct(loanRequestDTO.getAnnualInterestRate())
                .tenureMonths(loanRequestDTO.getTenureMonths())
                .build();

        EmiResponseDto emiResp = emiService.calculateEmi(emiReq);

        Loan loan = Loan.builder()
                .name(loanRequestDTO.getName())
                .principalAmount(loanRequestDTO.getPrincipalAmount())
                .annualInterestRate(loanRequestDTO.getAnnualInterestRate())
                .tenureMonths(loanRequestDTO.getTenureMonths())
                .startDate(loanRequestDTO.getStartDate())
                .status(Loan.LoanStatus.ACTIVE)
                .emi(emiResp.getEmi())
                .totalPayment(emiResp.getTotalPayment())
                .totalInterest(emiResp.getTotalInterest())
                .userId(loanRequestDTO.getUserId())
                .build();

        loan = loanRepository.save(loan);
        log.info("Created loan {} → EMI {} for user {}", loan.getId(), emiResp.getEmi(), loan.getUserId());
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

        EmiRequestDto emiReq = EmiRequestDto.builder()
                .principal(loan.getPrincipalAmount())
                .annualRatePct(loan.getAnnualInterestRate())
                .tenureMonths(loan.getTenureMonths())
                .build();
        EmiResponseDto emiResp = emiService.calculateEmi(emiReq);

        loan.setEmi(emiResp.getEmi());
        loan.setTotalPayment(emiResp.getTotalPayment());
        loan.setTotalInterest(emiResp.getTotalInterest());

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

    @Override
    public LoanDTO toggleLoanStatus(Integer loanId, Loan.LoanStatus newStatus) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFound("Loan not found with id " + loanId));
        loan.setStatus(newStatus);
        loanRepository.save(loan);
        return convertToLoanDTO(loan);
    }

    @Override
    public void evaluateAndMarkLoans() {
        List<Loan> allLoans = loanRepository.findAll();

        LocalDate today = LocalDate.now();
        for (Loan loan : allLoans) {
            LocalDate endDate = loan.getStartDate().plusMonths(loan.getTenureMonths());
            if (today.isAfter(endDate) && loan.getStatus() == Loan.LoanStatus.ACTIVE) {
                loan.setStatus(Loan.LoanStatus.CLOSED);
                loanRepository.save(loan);
                log.info("Auto-marked loan {} as CLOSED", loan.getId());
            }
        }
    }
}

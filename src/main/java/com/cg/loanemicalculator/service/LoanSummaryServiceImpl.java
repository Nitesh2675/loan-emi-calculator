package com.cg.loanemicalculator.service;

import com.cg.loanemicalculator.dto.LoanSummaryDto;
import com.cg.loanemicalculator.dto.LoanSummaryEntryDto;
import com.cg.loanemicalculator.model.Loan;
import com.cg.loanemicalculator.model.Prepayment;
import com.cg.loanemicalculator.model.User;
import com.cg.loanemicalculator.repository.LoanRepository;
import com.cg.loanemicalculator.repository.PrepaymentRepository;
import com.cg.loanemicalculator.repository.UserRepository;
import com.cg.loanemicalculator.security.ResourceOwnershipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanSummaryServiceImpl implements LoanSummaryService {

    private final LoanRepository               loanRepository;
    private final PrepaymentRepository         prepaymentRepository;
    private final UserRepository               userRepository;
    private final ResourceOwnershipValidator   authz;

    @Override
    public LoanSummaryDto getLoanSummaryForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        List<LoanSummaryEntryDto> entries = loanRepository
                .findByUserId(userId.intValue())
                .stream()
                .peek(loan -> authz.validateLoanOwnership(loan, userId.intValue()))
                .map(this::toEntryDto)
                .collect(Collectors.toList());

        LoanSummaryDto dto = new LoanSummaryDto();
        dto.setUserId(userId.intValue());
        dto.setUserName(user.getEmail());      // use getEmail() instead of getName()
        dto.setLoans(entries);
        return dto;
    }

    private LoanSummaryEntryDto toEntryDto(Loan loan) {
        var totalPrepayments = prepaymentRepository
                .findByLoan(loan)
                .stream()
                .map(Prepayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var start = loan.getStartDate();
        var end   = start.plusMonths(loan.getTenureMonths());

        return new LoanSummaryEntryDto(
                loan.getId(),
                loan.getPrincipalAmount(),
                loan.getOutstandingPrincipal(),
                loan.getTotalInterest(),
                start,
                end,
                loan.getEmi(),
                totalPrepayments
        );
    }
}

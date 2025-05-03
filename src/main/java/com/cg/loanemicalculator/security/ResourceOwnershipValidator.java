package com.cg.loanemicalculator.security;

import com.cg.loanemicalculator.exception.AccessDeniedException;
import com.cg.loanemicalculator.model.Loan;
import org.springframework.stereotype.Component;

@Component
public class ResourceOwnershipValidator {
    public void validateLoanOwnership(Loan loan, Integer userId) {
        if (!loan.getUserId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to modify loan " + loan.getId());
        }
    }
}

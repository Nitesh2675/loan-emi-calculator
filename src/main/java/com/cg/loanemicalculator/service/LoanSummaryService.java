package com.cg.loanemicalculator.service;

import com.cg.loanemicalculator.dto.LoanSummaryDto;

public interface LoanSummaryService {
    LoanSummaryDto getLoanSummaryForUser(Long userId);
}

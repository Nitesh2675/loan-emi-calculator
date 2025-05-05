package com.cg.loanemicalculator.util;

import com.cg.loanemicalculator.dto.LoanSummaryDto;
import com.cg.loanemicalculator.exception.ReportGenerationException;

public interface ReportGenerator {
    /**
     * @param summary fully-populated summary DTO
     * @return raw PDF bytes
     */
    byte[] generateLoanSummaryPdf(LoanSummaryDto summary) throws ReportGenerationException;
}

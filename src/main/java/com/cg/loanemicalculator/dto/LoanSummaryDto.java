package com.cg.loanemicalculator.dto;

import lombok.Data;

import java.util.List;

@Data
public class LoanSummaryDto {
    private Integer userId;       // now Integer, not Long
    private String  userName;     // we’ll pull email
    private List<LoanSummaryEntryDto> loans;
}

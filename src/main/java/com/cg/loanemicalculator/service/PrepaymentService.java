package com.cg.loanemicalculator.service;

import com.cg.loanemicalculator.dto.PrepaymentDTO;
import com.cg.loanemicalculator.dto.PrepaymentRequestDTO;

import java.util.List;

public interface PrepaymentService {
    PrepaymentDTO createPrepayment(Integer loanId, PrepaymentRequestDTO prepaymentRequestDTOtDTO, Integer userId);
    List<PrepaymentDTO> getPrepayments(Integer loanId, Integer userId);
}

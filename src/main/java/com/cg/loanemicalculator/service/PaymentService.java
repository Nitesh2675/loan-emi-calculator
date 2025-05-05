package com.cg.loanemicalculator.service;

import com.cg.loanemicalculator.dto.PaymentDTO;
import com.cg.loanemicalculator.dto.PaymentRequestDTO;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {
    PaymentDTO addPayment(Integer loanId, Integer userId);
    List<PaymentDTO> getPayments(Integer loanId, Integer userId);
}

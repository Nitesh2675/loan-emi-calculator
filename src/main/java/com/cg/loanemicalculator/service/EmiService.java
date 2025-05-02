package com.cg.loanemicalculator.service;

import com.cg.loanemicalculator.dto.EmiRequestDto;
import com.cg.loanemicalculator.dto.EmiResponseDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class EmiService {

    public EmiResponseDto calculateEmi(EmiRequestDto req) {
        BigDecimal P = req.getPrincipal();
        BigDecimal annualRate = req.getAnnualRatePct();
        int n = req.getTenureMonths();

        // r = monthly rate = annualRate/12/100
        BigDecimal r = annualRate
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_EVEN)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_EVEN);

        BigDecimal onePlusRn = (BigDecimal.ONE.add(r)).pow(n);
        BigDecimal numerator   = P.multiply(r).multiply(onePlusRn);
        BigDecimal denominator = onePlusRn.subtract(BigDecimal.ONE);
        BigDecimal emi = numerator.divide(denominator, 2, RoundingMode.HALF_EVEN);

        BigDecimal totalPayment  = emi.multiply(BigDecimal.valueOf(n));
        BigDecimal totalInterest = totalPayment.subtract(P);

        return EmiResponseDto.builder()
                .emi(emi)
                .totalPayment(totalPayment)
                .totalInterest(totalInterest)
                .build();
    }
}


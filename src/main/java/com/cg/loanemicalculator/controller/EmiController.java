package com.cg.loanemicalculator.controller;

import com.cg.loanemicalculator.dto.EmiRequestDto;
import com.cg.loanemicalculator.dto.EmiResponseDto;
import com.cg.loanemicalculator.service.EmiService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/emi")
@Validated
public class EmiController {

    private final EmiService emiService;

    public EmiController(EmiService emiService) {
        this.emiService = emiService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<EmiResponseDto> calculateEmi(
            @Valid @RequestBody EmiRequestDto req) {
        EmiResponseDto resp = emiService.calculateEmi(req);
        return ResponseEntity.ok(resp);
    }
}

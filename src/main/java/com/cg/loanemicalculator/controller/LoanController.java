package com.cg.loanemicalculator.controller;

import com.cg.loanemicalculator.dto.EmiRequestDto;
import com.cg.loanemicalculator.dto.EmiResponseDto;
import com.cg.loanemicalculator.dto.LoanDTO;
import com.cg.loanemicalculator.dto.LoanRequestDTO;
import com.cg.loanemicalculator.service.EmiService;
import com.cg.loanemicalculator.service.LoanService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cg.loanemicalculator.model.Loan;

import java.util.List;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final EmiService emiService;

    @PostMapping
    public ResponseEntity<LoanDTO> createLoan(HttpServletRequest request, @RequestBody LoanRequestDTO loanRequestDTO) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId != null) {
            loanRequestDTO.setUserId(userId);
        }
        LoanDTO loanDTO = loanService.createLoan(loanRequestDTO);
        return new ResponseEntity<>(loanDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoanDTO> updateLoan(@RequestBody LoanRequestDTO loanRequestDTO, @PathVariable Integer id) {
        LoanDTO loanDTO = loanService.updateLoan(id, loanRequestDTO);
        return new ResponseEntity<>(loanDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Integer id) {
        loanService.deleteLoan(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

//    @GetMapping("/{userId}")
//    public ResponseEntity<List<LoanDTO>> getUserLoans(@PathVariable Integer userId) {
//        return new ResponseEntity<>(loanService.getUserLoans(userId), HttpStatus.OK);
//    }
@GetMapping("")
public ResponseEntity<List<LoanDTO>> getUserLoans(HttpServletRequest request) {
    Integer userId = (Integer) request.getAttribute("userId");

    if (userId == null) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    List<LoanDTO> loans = loanService.getUserLoans(userId);
    return new ResponseEntity<>(loans, HttpStatus.OK);
}


    @PostMapping("/calculate-emi")
    public ResponseEntity<EmiResponseDto> calculateEmi(
            @Valid @RequestBody EmiRequestDto req) {
        EmiResponseDto resp = emiService.calculateEmi(req);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<LoanDTO> toggleLoanStatus(@PathVariable Integer id, @RequestParam String status) {
        Loan.LoanStatus newStatus = Loan.LoanStatus.valueOf(status.toUpperCase());
        LoanDTO updatedLoan = loanService.toggleLoanStatus(id, newStatus);
        return new ResponseEntity<>(updatedLoan, HttpStatus.OK);
    }

    @GetMapping("/evaluate-status")
    public ResponseEntity<Void> evaluateAndMarkLoanStatuses() {
        loanService.evaluateAndMarkLoans();
        return new ResponseEntity<>(HttpStatus.OK);
    }


}

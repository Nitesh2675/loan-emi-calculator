package com.cg.loanemicalculator.controller;

import com.cg.loanemicalculator.dto.*;
import com.cg.loanemicalculator.model.Loan;
import com.cg.loanemicalculator.service.EmiService;
import com.cg.loanemicalculator.service.LoanService;
import com.cg.loanemicalculator.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final EmiService emiService;
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<LoanDTO> createLoan(
            HttpServletRequest request,
            @RequestBody LoanRequestDTO loanRequestDTO) {

        Integer userId = (Integer) request.getAttribute("userId");
        loanRequestDTO.setUserId(userId);
        LoanDTO loanDTO = loanService.createLoan(loanRequestDTO);
        return new ResponseEntity<>(loanDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoanDTO> updateLoan(
            HttpServletRequest request,
            @RequestBody LoanRequestDTO loanRequestDTO,
            @PathVariable Integer id) {

        Integer userId = (Integer) request.getAttribute("userId");
        loanRequestDTO.setUserId(userId);
        LoanDTO loanDTO = loanService.updateLoan(id, loanRequestDTO);
        return new ResponseEntity<>(loanDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(
            HttpServletRequest request,
            @PathVariable Integer id) {

        Integer userId = (Integer) request.getAttribute("userId");
        loanService.deleteLoan(id, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<LoanDTO>> getUserLoans(
            HttpServletRequest request) {

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
    public ResponseEntity<LoanDTO> toggleLoanStatus(
            HttpServletRequest request,
            @PathVariable Integer id,
            @RequestParam String status) {

        Integer userId = (Integer) request.getAttribute("userId");
        Loan.LoanStatus newStatus = Loan.LoanStatus.valueOf(status.trim().toUpperCase());
        LoanDTO updatedLoan = loanService.toggleLoanStatus(id, newStatus, userId);
        return new ResponseEntity<>(updatedLoan, HttpStatus.OK);
    }

    @GetMapping("/evaluate-status")
    public ResponseEntity<Void> evaluateAndMarkLoanStatuses() {
        loanService.evaluateAndMarkLoans();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{loanId}/payments")
    public ResponseEntity<List<PaymentDTO>> addPayment(HttpServletRequest request, @PathVariable Integer loanId) {
        Integer userId = (Integer) request.getAttribute("userId");
        List<PaymentDTO> paymentDTOList = paymentService.getPayments(loanId, userId);
        return new ResponseEntity<>(paymentDTOList, HttpStatus.OK);
    }

    @PostMapping("/{loanId}/payment")
    public ResponseEntity<PaymentDTO> getPayment(HttpServletRequest request, @PathVariable Integer loanId) {
        Integer userId = (Integer) request.getAttribute("userId");
        PaymentDTO paymentDTO = paymentService.addPayment(loanId, userId);
        return new ResponseEntity<>(paymentDTO, HttpStatus.CREATED);
    }
}

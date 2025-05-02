package com.cg.loanemicalculator.controller;

import com.cg.loanemicalculator.dto.LoanDTO;
import com.cg.loanemicalculator.dto.LoanRequestDTO;
import com.cg.loanemicalculator.service.LoanService;
import jakarta.servlet.http.HttpServletRequest;
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

    @GetMapping("/{userId}")
    public ResponseEntity<List<LoanDTO>> getUserLoans(@PathVariable Integer userId) {
        return new ResponseEntity<>(loanService.getUserLoans(userId), HttpStatus.OK);
    }

}

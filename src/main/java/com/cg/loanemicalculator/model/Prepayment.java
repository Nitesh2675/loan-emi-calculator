package com.cg.loanemicalculator.model;

import com.cg.loanemicalculator.dto.PrepaymentRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prepayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @ManyToOne
    @JoinColumn(name = "loan_id")
    private Loan loan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrepaymentType prepaymentType;

}
/*

    @GetMapping("/{loanId}/prepayments")
    public ResponseEntity<List<PrepaymentDTO>> getPrepayments(HttpServletRequest request, @PathVariable Integer loanId) {
        Integer userId = (Integer) request.getAttribute("userId");
        List<PrepaymentDTO> prepaymentDTOList = prepaymentService.getPrepayments(loanId, userId);
        return new ResponseEntity<>(prepaymentDTOList, HttpStatus.OK);
    }

    @PostMapping("/{loanId}/prepayment")
    public ResponseEntity<PrepaymentDTO> createPrepayment(HttpServletRequest request, @PathVariable Integer loanId, @RequestBody PrepaymentRequestDTO prepaymentRequestDTO) {
        Integer userId = (Integer) request.getAttribute("userId");
        PrepaymentDTO prepaymentDTO = prepaymentService.createPrepayment(loanId, prepaymentRequestDTO, userId);
        return new ResponseEntity<>(prepaymentDTO, HttpStatus.CREATED);
    }

 */
package com.cg.loanemicalculator.repository;

import com.cg.loanemicalculator.model.AmortizationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmortizationScheduleRepository extends JpaRepository<AmortizationSchedule, Long> {
    List<AmortizationSchedule> findByLoanId(Integer loanId);
    boolean existsByLoanId(Integer loanId);

    List<AmortizationSchedule> findByLoanIdOrderByIdAsc(Integer loanId);
}

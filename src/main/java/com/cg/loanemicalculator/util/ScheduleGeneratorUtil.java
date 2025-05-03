package com.cg.loanemicalculator.util;

import com.cg.loanemicalculator.dto.AmortizationScheduleEntryDto;
import com.cg.loanemicalculator.model.Loan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ScheduleGeneratorUtil {

    public static List<AmortizationScheduleEntryDto> generateSchedule(Loan loan) {
        List<AmortizationScheduleEntryDto> schedule = new ArrayList<>();

        BigDecimal balance = loan.getPrincipalAmount();
        BigDecimal emi = loan.getEmi();
        BigDecimal monthlyInterestRate = loan.getAnnualInterestRate()
                .divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_EVEN);
        LocalDate paymentDate = loan.getStartDate();

        for (int month = 1; month <= loan.getTenureMonths(); month++) {
            BigDecimal interestComponent = balance.multiply(monthlyInterestRate)
                    .setScale(2, RoundingMode.HALF_EVEN);

            BigDecimal principalComponent = emi.subtract(interestComponent)
                    .setScale(2, RoundingMode.HALF_EVEN);

            // Prevent negative balance in the last installment
            if (principalComponent.compareTo(balance) > 0) {
                principalComponent = balance;
                emi = principalComponent.add(interestComponent);
            }

            BigDecimal endingBalance = balance.subtract(principalComponent)
                    .setScale(2, RoundingMode.HALF_EVEN);

            schedule.add(new AmortizationScheduleEntryDto(
                    month,
                    paymentDate,
                    balance,
                    emi,
                    principalComponent,
                    interestComponent,
                    endingBalance
            ));

            balance = endingBalance;
            paymentDate = paymentDate.plusMonths(1);

            if (balance.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
        }

        return schedule;
    }
}

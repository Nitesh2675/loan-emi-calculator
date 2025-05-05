package com.cg.loanemicalculator.util;

import com.cg.loanemicalculator.dto.AmortizationScheduleEntryDto;
import com.cg.loanemicalculator.model.Loan;
import com.cg.loanemicalculator.model.PrepaymentType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ScheduleGeneratorUtil {

    public static List<AmortizationScheduleEntryDto> recalculateFromMonth(
            Loan loan,
            int startMonth,
            LocalDate startDate,
            PrepaymentType prepaymentType
    ) {

        List<AmortizationScheduleEntryDto> schedule = new ArrayList<>();
        BigDecimal updatedPrincipal = loan.getOutstandingPrincipal();

        BigDecimal balance = updatedPrincipal;
        BigDecimal monthlyInterestRate = loan.getAnnualInterestRate()
                .divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_EVEN);
        LocalDate paymentDate = startDate;

        int remainingMonths = loan.getTenureMonths() - (startMonth - 1);
        BigDecimal emi;

        if (prepaymentType == PrepaymentType.REDUCE_TENURE) {
            // Use same EMI, reduce tenure dynamically
            emi = loan.getEmi();

            for (int month = startMonth; ; month++) {
                BigDecimal interestComponent = balance.multiply(monthlyInterestRate)
                        .setScale(2, RoundingMode.HALF_EVEN);

                BigDecimal principalComponent = emi.subtract(interestComponent)
                        .setScale(2, RoundingMode.HALF_EVEN);

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
                        endingBalance,
                        false
                ));

                balance = endingBalance;
                paymentDate = paymentDate.plusMonths(1);

                if (balance.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }
            }

        } else if (prepaymentType == PrepaymentType.REDUCE_EMI) {
            Loan newLoan = new Loan();
            newLoan.setPrincipalAmount(updatedPrincipal);
            newLoan.setStartDate(startDate);
            newLoan.setAnnualInterestRate(loan.getAnnualInterestRate());
            newLoan.setTenureMonths(loan.getTenureMonths() - (startMonth - 1));

            // Recalculate EMI with new balance and remaining months
            BigDecimal r = newLoan.getAnnualInterestRate()
                    .divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_EVEN);
            int n = newLoan.getTenureMonths();
            BigDecimal onePlusRPowerN = r.add(BigDecimal.ONE).pow(n);
            BigDecimal newEMI = updatedPrincipal.multiply(r).multiply(onePlusRPowerN)
                    .divide(onePlusRPowerN.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_EVEN);

            newLoan.setEmi(newEMI);

            schedule = generateSchedule(newLoan);
        }

        return schedule;
    }

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

            // Prevent overpayment in the final installment
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
                    endingBalance,
                    false // Default to ✗ unpaid
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

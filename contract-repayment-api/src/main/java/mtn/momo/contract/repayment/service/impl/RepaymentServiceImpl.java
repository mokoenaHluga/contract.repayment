package mtn.momo.contract.repayment.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mtn.momo.contract.repayment.exception.AnnualRateException;
import mtn.momo.contract.repayment.model.dto.RepaymentOption;
import mtn.momo.contract.repayment.model.request.RepaymentRequest;
import mtn.momo.contract.repayment.service.ICacheService;
import mtn.momo.contract.repayment.service.IRepaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepaymentServiceImpl implements IRepaymentService {
    private final ICacheService cacheService;

//    @Value("${repayment.rate}")
    private Double repaymentRate = 6.5;

    /**
     * Calculate repayment options for a given loan amount as per different terms specified in the properties.
     *
     * @param request RepaymentRequest containing the principal loan amount
     * @return List of RepaymentOption, each containing a term and the calculated monthly payment for that term
     */
    @Override
    public List<RepaymentOption> calculateRepaymentOptions(RepaymentRequest request) throws AnnualRateException {
        log.info("Calculating repayment options for amount: {}", request.getAmount());

        List<RepaymentOption> options = new ArrayList<>();
        List<Integer> terms = convertStringToList(cacheService.getConfigValue("repayment.terms"));

        for (Integer term : terms) {
            BigDecimal monthlyPayment = calculateMonthlyPayment(BigDecimal.valueOf(request.getAmount()), term);
            options.add(new RepaymentOption(term, monthlyPayment));
        }
        return options;
    }

    /**
     * Calculates the monthly payment for a given loan amount and term using the formula for amortizing loans.
     *
     * @param amount The principal amount of the loan
     * @param term The term in months over which the loan is repaid
     * @return BigDecimal representing the monthly payment amount
     */
    private BigDecimal calculateMonthlyPayment(BigDecimal amount, int term) throws AnnualRateException {
        BigDecimal monthlyRate = calculateMonthlyInterestRate();
        BigDecimal powFactor = BigDecimal.ONE.add(monthlyRate).pow(term, MathContext.DECIMAL128);
        BigDecimal denominator = powFactor.subtract(BigDecimal.ONE);
        // Return the monthly payment calculated using the formula: P * r * (1+r)^n / ((1+r)^n - 1)
        return amount.multiply(monthlyRate).multiply(powFactor).divide(denominator, 2, BigDecimal.ROUND_HALF_EVEN);
    }

    /**
     * Retrieves the annual interest rate from properties file, converts it to a monthly rate in decimal form.
     *
     * @return BigDecimal representing the monthly interest rate as a decimal
     */
    private BigDecimal calculateMonthlyInterestRate() throws AnnualRateException {
        if (repaymentRate == null) {
            throw new AnnualRateException("Annual rate is not available.");
        }
        BigDecimal annualRate = BigDecimal.valueOf(repaymentRate);
        BigDecimal decimalRate = annualRate.divide(BigDecimal.valueOf(100), MathContext.DECIMAL128);
        return decimalRate.divide(BigDecimal.valueOf(12), MathContext.DECIMAL128);
    }

    /**
     * Converts a comma-separated string of integers into a list of Integer.
     *
     * @param termString The comma-separated string e.g. "12,24,36".
     * @return A list of integers extracted from the string.
     */
    public List<Integer> convertStringToList(String termString) {
        if (termString == null || termString.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(termString.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}

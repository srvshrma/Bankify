package com.bank.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

@FunctionalInterface
public interface InterestCalculator {

    BigDecimal calculate(BigDecimal balance, BigDecimal rate);

    default String describe(BigDecimal balance, BigDecimal rate) {
        return "Projected annual interest: " + calculate(balance, rate).setScale(2, RoundingMode.HALF_UP);
    }

    static InterestCalculator simpleInterest() {
        return (balance, rate) -> balance.multiply(rate);
    }
}

package com.ricemill.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculationUtil {

    /**
     * Calculate subtotal
     */
    public static BigDecimal calculateSubtotal(BigDecimal quantity, BigDecimal pricePerKg) {
        return quantity.multiply(pricePerKg).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate discount amount from percentage
     */
    public static BigDecimal calculateDiscountAmount(BigDecimal subtotal, BigDecimal discountPercentage) {
        if (discountPercentage == null || discountPercentage.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return subtotal.multiply(discountPercentage)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate tax amount from percentage
     */
    public static BigDecimal calculateTaxAmount(BigDecimal amountAfterDiscount, BigDecimal taxPercentage) {
        if (taxPercentage == null || taxPercentage.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return amountAfterDiscount.multiply(taxPercentage)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate total amount for paddy sale
     */
    public static BigDecimal calculatePaddyTotal(BigDecimal subtotal,
                                                  BigDecimal discountAmount,
                                                  BigDecimal deliveryCharge) {
        return subtotal
                .subtract(discountAmount)
                .add(deliveryCharge)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate total amount for rice sale
     */
    public static BigDecimal calculateRiceTotal(BigDecimal subtotal,
                                                 BigDecimal discountAmount,
                                                 BigDecimal taxAmount,
                                                 BigDecimal deliveryCharge) {
        return subtotal
                .subtract(discountAmount)
                .add(taxAmount)
                .add(deliveryCharge)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate balance amount
     */
    public static BigDecimal calculateBalance(BigDecimal totalAmount, BigDecimal paidAmount) {
        BigDecimal balance = totalAmount.subtract(paidAmount);
        return balance.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : balance;
    }
}


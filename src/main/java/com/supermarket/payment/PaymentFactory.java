package com.supermarket.payment;

import com.supermarket.dto.BillRequest;

/**
 * Factory Design Pattern to create appropriate PaymentStrategy instance dynamically.
 * Demonstrates Abstraction and Factory OOP design pattern.
 */
public class PaymentFactory {

    public static PaymentStrategy createPayment(BillRequest request) {
        String mode = request.getPaymentMode() != null ? request.getPaymentMode().toUpperCase() : "CASH";

        switch (mode) {
            case "UPI":
                return new UpiPayment(request.getUpiId(), request.getTransactionRef());

            case "CARD":
                return new CardPayment(request.getCardType(), request.getLastFourDigits());

            case "CASH":
            default:
                CashPayment cash = new CashPayment();
                if (request.getCashTendered() > 0) {
                    cash.setCashTendered(request.getCashTendered());
                }
                return cash;
        }
    }
}

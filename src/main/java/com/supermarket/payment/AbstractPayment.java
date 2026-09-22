package com.supermarket.payment;

import java.util.UUID;

/**
 * Abstract Payment Base Class demonstrating Inheritance and Encapsulation.
 */
public abstract class AbstractPayment implements PaymentStrategy {
    private String transactionId;

    public AbstractPayment() {
        this.transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String generateQrPayload(double amount, String billNumber) {
        return ""; // Default: No QR code for Cash/Card
    }
}

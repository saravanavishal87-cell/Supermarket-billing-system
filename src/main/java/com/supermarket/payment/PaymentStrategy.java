package com.supermarket.payment;

/**
 * Strategy Pattern Interface for Payment Methods.
 * Demonstrates Polymorphism & Abstraction OOP principles.
 */
public interface PaymentStrategy {
    String getModeName();
    boolean processPayment(double amount);
    String getTransactionId();
    String getPaymentDetails();
    String generateQrPayload(double amount, String billNumber);
}

package com.supermarket.payment;

/**
 * Concrete Card Payment class demonstrating Polymorphism.
 */
public class CardPayment extends AbstractPayment {
    private String cardType;
    private String lastFourDigits;

    public CardPayment() {
        this.cardType = "DEBIT/CREDIT";
        this.lastFourDigits = "XXXX";
    }

    public CardPayment(String cardType, String lastFourDigits) {
        this.cardType = (cardType != null && !cardType.isBlank()) ? cardType : "CARD";
        this.lastFourDigits = (lastFourDigits != null && !lastFourDigits.isBlank()) ? lastFourDigits : "4321";
    }

    @Override
    public String getModeName() {
        return "CARD";
    }

    @Override
    public boolean processPayment(double amount) {
        return amount > 0;
    }

    @Override
    public String getPaymentDetails() {
        return String.format("Card: %s ending in %s | Auth Ref: %s", cardType, lastFourDigits, getTransactionId());
    }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public String getLastFourDigits() { return lastFourDigits; }
    public void setLastFourDigits(String lastFourDigits) { this.lastFourDigits = lastFourDigits; }
}

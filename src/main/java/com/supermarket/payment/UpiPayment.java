package com.supermarket.payment;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Concrete UPI Payment class demonstrating Polymorphism and Dynamic QR Generation.
 */
public class UpiPayment extends AbstractPayment {
    private String upiId;
    private String upiTxnRef;

    public UpiPayment() {
        this.upiId = "saravanavishal06@oksbi";
    }

    public UpiPayment(String upiId, String upiTxnRef) {
        this.upiId = (upiId != null && !upiId.isBlank()) ? upiId : "saravanavishal06@oksbi";
        this.upiTxnRef = upiTxnRef;
    }

    @Override
    public String getModeName() {
        return "UPI";
    }

    @Override
    public boolean processPayment(double amount) {
        return amount >= 0;
    }

    @Override
    public String getPaymentDetails() {
        return "UPI ID: " + upiId + " | Ref: " + getTransactionId();
    }

    @Override
    public String generateQrPayload(double amount, String billNumber) {
        String pn = URLEncoder.encode("SuperMarket POS", StandardCharsets.UTF_8);
        String tn = URLEncoder.encode("Bill " + (billNumber != null ? billNumber : "Checkout"), StandardCharsets.UTF_8);
        return String.format("upi://pay?pa=%s&pn=%s&am=%.2f&cu=INR&tn=%s", upiId, pn, amount, tn);
    }

    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }

    public String getUpiTxnRef() { return upiTxnRef; }
    public void setUpiTxnRef(String upiTxnRef) { this.upiTxnRef = upiTxnRef; }
}

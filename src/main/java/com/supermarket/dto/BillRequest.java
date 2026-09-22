package com.supermarket.dto;

import com.supermarket.model.BillItem;
import java.util.List;

public class BillRequest {
    private int cashierId;
    private Integer customerId;
    private String customerName;
    private String customerPhone;
    private List<BillItem> items;
    private double discount;
    private String paymentMode;

    // Payment details fields
    private double cashTendered;
    private String upiId;
    private String transactionRef;
    private String cardType;
    private String lastFourDigits;

    public int getCashierId() { return cashierId; }
    public void setCashierId(int cashierId) { this.cashierId = cashierId; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public List<BillItem> getItems() { return items; }
    public void setItems(List<BillItem> items) { this.items = items; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public double getCashTendered() { return cashTendered; }
    public void setCashTendered(double cashTendered) { this.cashTendered = cashTendered; }

    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public String getLastFourDigits() { return lastFourDigits; }
    public void setLastFourDigits(String lastFourDigits) { this.lastFourDigits = lastFourDigits; }
}

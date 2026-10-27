package com.supermarket.model;

import java.util.ArrayList;
import java.util.List;

public class Bill {

    private int billId;
    private String billNumber;
    private int cashierId;
    private String customerName;
    private Integer customerId;
    private String customerPhone;
    private List<BillItem> items;
    private double subtotal;
    private double discount;
    private double taxAmount;
    private double grandTotal;
    private String paymentMode;
    private java.sql.Timestamp billDate;

    public Bill() {
        this.items = new ArrayList<>();
        this.customerName = "Walk-in Customer";
    }

    public void addItem(BillItem item) {
        for (BillItem existing : items) {
            if (existing.getProductId() == item.getProductId()) {
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                recalculate();
                return;
            }
        }
        items.add(item);
        recalculate();
    }

    public void recalculate() {
        subtotal = Math.round(items.stream().mapToDouble(BillItem::getLineTotal).sum() * 100.0) / 100.0;
        double taxable = Math.max(0, Math.round((subtotal - discount) * 100.0) / 100.0);
        taxAmount = (taxable > 1000.0) ? (Math.round(taxable * 0.05 * 100.0) / 100.0) : 0.0;
        grandTotal = Math.round((taxable + taxAmount) * 100.0) / 100.0;
    }

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }

    public int getCashierId() { return cashierId; }
    public void setCashierId(int cashierId) { this.cashierId = cashierId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public List<BillItem> getItems() { return items; }
    public void setItems(List<BillItem> items) {
        this.items = items;
        recalculate();
    }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) {
        this.discount = discount;
        recalculate();
    }

    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }

    public double getGrandTotal() { return grandTotal; }
    public void setGrandTotal(double grandTotal) { this.grandTotal = grandTotal; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public java.sql.Timestamp getBillDate() { return billDate; }
    public void setBillDate(java.sql.Timestamp billDate) { this.billDate = billDate; }
}

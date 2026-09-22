package com.supermarket.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Expense {
    private int expenseId;
    private Date expenseDate;
    private String category; // 'Rent', 'Electric Bill', 'Purchases', 'Salary', 'Maintenance', 'Other'
    private double amount;
    private String description;
    private String paymentMode;
    private Integer recordedBy;
    private String recordedByName;
    private Timestamp createdAt;

    public Expense() {}

    public Expense(int expenseId, Date expenseDate, String category, double amount, 
                   String description, String paymentMode, Integer recordedBy, 
                   String recordedByName, Timestamp createdAt) {
        this.expenseId = expenseId;
        this.expenseDate = expenseDate;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.paymentMode = paymentMode;
        this.recordedBy = recordedBy;
        this.recordedByName = recordedByName;
        this.createdAt = createdAt;
    }

    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public Date getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(Date expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Integer getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(Integer recordedBy) {
        this.recordedBy = recordedBy;
    }

    public String getRecordedByName() {
        return recordedByName;
    }

    public void setRecordedByName(String recordedByName) {
        this.recordedByName = recordedByName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}

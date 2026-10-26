package com.supermarket.dto;

public class AiExpiryItem {
    private int productId;
    private String productName;
    private String categoryName;
    private double price;
    private double stockQty;
    private String unit;
    private String expiryDate;
    private String batchNo;
    private long daysRemaining;
    private String status; // EXPIRED, CRITICAL, WARNING, HEALTHY
    private String statusLabel;
    private double wasteRiskValue;
    private int aiRecommendedDiscount; // percentage: 0, 15, 30, 50
    private double discountedPrice;
    private double estimatedSalvageValue;
    private String aiReasoning;

    public AiExpiryItem() {}

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getStockQty() { return stockQty; }
    public void setStockQty(double stockQty) { this.stockQty = stockQty; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }

    public long getDaysRemaining() { return daysRemaining; }
    public void setDaysRemaining(long daysRemaining) { this.daysRemaining = daysRemaining; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStatusLabel() { return statusLabel; }
    public void setStatusLabel(String statusLabel) { this.statusLabel = statusLabel; }

    public double getWasteRiskValue() { return wasteRiskValue; }
    public void setWasteRiskValue(double wasteRiskValue) { this.wasteRiskValue = wasteRiskValue; }

    public int getAiRecommendedDiscount() { return aiRecommendedDiscount; }
    public void setAiRecommendedDiscount(int aiRecommendedDiscount) { this.aiRecommendedDiscount = aiRecommendedDiscount; }

    public double getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(double discountedPrice) { this.discountedPrice = discountedPrice; }

    public double getEstimatedSalvageValue() { return estimatedSalvageValue; }
    public void setEstimatedSalvageValue(double estimatedSalvageValue) { this.estimatedSalvageValue = estimatedSalvageValue; }

    public String getAiReasoning() { return aiReasoning; }
    public void setAiReasoning(String aiReasoning) { this.aiReasoning = aiReasoning; }
}

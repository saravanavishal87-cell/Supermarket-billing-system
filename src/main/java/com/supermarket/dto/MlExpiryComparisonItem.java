package com.supermarket.dto;

public class MlExpiryComparisonItem {
    private int productId;
    private String productName;
    private String categoryName;
    private double price;
    private double stockQty;
    private String unit;
    private String expiryDate;
    private long daysRemaining;
    private double wasteRiskValue;

    // Classification
    private String wekaClassification;
    private String smileClassification;
    private String consensusClassification;
    private int wekaRecommendedDiscount;
    private int smileRecommendedDiscount;
    private int consensusDiscount;
    private double discountedPrice;
    private double salvageRevenue;
    private String recommendationReason;

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

    public long getDaysRemaining() { return daysRemaining; }
    public void setDaysRemaining(long daysRemaining) { this.daysRemaining = daysRemaining; }

    public double getWasteRiskValue() { return wasteRiskValue; }
    public void setWasteRiskValue(double wasteRiskValue) { this.wasteRiskValue = wasteRiskValue; }

    public String getWekaClassification() { return wekaClassification; }
    public void setWekaClassification(String wekaClassification) { this.wekaClassification = wekaClassification; }

    public String getSmileClassification() { return smileClassification; }
    public void setSmileClassification(String smileClassification) { this.smileClassification = smileClassification; }

    public String getConsensusClassification() { return consensusClassification; }
    public void setConsensusClassification(String consensusClassification) { this.consensusClassification = consensusClassification; }

    public int getWekaRecommendedDiscount() { return wekaRecommendedDiscount; }
    public void setWekaRecommendedDiscount(int wekaRecommendedDiscount) { this.wekaRecommendedDiscount = wekaRecommendedDiscount; }

    public int getSmileRecommendedDiscount() { return smileRecommendedDiscount; }
    public void setSmileRecommendedDiscount(int smileRecommendedDiscount) { this.smileRecommendedDiscount = smileRecommendedDiscount; }

    public int getConsensusDiscount() { return consensusDiscount; }
    public void setConsensusDiscount(int consensusDiscount) { this.consensusDiscount = consensusDiscount; }

    public double getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(double discountedPrice) { this.discountedPrice = discountedPrice; }

    public double getSalvageRevenue() { return salvageRevenue; }
    public void setSalvageRevenue(double salvageRevenue) { this.salvageRevenue = salvageRevenue; }

    public String getRecommendationReason() { return recommendationReason; }
    public void setRecommendationReason(String recommendationReason) { this.recommendationReason = recommendationReason; }
}

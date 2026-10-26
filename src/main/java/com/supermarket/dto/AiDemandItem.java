package com.supermarket.dto;

public class AiDemandItem {
    private int productId;
    private String productName;
    private String categoryName;
    private double price;
    private double stockQty;
    private String unit;
    private double unitsSold; // past 30 days
    private double dailyVelocity; // units sold per day
    private double estimatedDaysLeft; // days until stockout
    private String demandTier; // HIGH_DEMAND, MODERATE_DEMAND, LOW_DEMAND, STOCKOUT_RISK
    private String demandTierLabel;
    private String trend; // RISING, STABLE, DECLINING
    private double aiRecommendedOrderQty;
    private String stockStatus; // CRITICAL_LOW, OPTIMAL, OVERSTOCKED
    private String aiInsight;

    public AiDemandItem() {}

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

    public double getUnitsSold() { return unitsSold; }
    public void setUnitsSold(double unitsSold) { this.unitsSold = unitsSold; }

    public double getDailyVelocity() { return dailyVelocity; }
    public void setDailyVelocity(double dailyVelocity) { this.dailyVelocity = dailyVelocity; }

    public double getEstimatedDaysLeft() { return estimatedDaysLeft; }
    public void setEstimatedDaysLeft(double estimatedDaysLeft) { this.estimatedDaysLeft = estimatedDaysLeft; }

    public String getDemandTier() { return demandTier; }
    public void setDemandTier(String demandTier) { this.demandTier = demandTier; }

    public String getDemandTierLabel() { return demandTierLabel; }
    public void setDemandTierLabel(String demandTierLabel) { this.demandTierLabel = demandTierLabel; }

    public String getTrend() { return trend; }
    public void setTrend(String trend) { this.trend = trend; }

    public double getAiRecommendedOrderQty() { return aiRecommendedOrderQty; }
    public void setAiRecommendedOrderQty(double aiRecommendedOrderQty) { this.aiRecommendedOrderQty = aiRecommendedOrderQty; }

    public String getStockStatus() { return stockStatus; }
    public void setStockStatus(String stockStatus) { this.stockStatus = stockStatus; }

    public String getAiInsight() { return aiInsight; }
    public void setAiInsight(String aiInsight) { this.aiInsight = aiInsight; }
}

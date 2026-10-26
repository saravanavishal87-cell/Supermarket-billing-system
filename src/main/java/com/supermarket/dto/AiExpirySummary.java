package com.supermarket.dto;

import java.util.List;

public class AiExpirySummary {
    private int totalProducts;
    private int expiredCount;
    private int criticalCount; // 1-7 days
    private int warningCount;  // 8-30 days
    private int healthyCount;  // > 30 days
    private double totalWasteRiskValue;
    private double totalSalvageableValue;
    private List<AiExpiryItem> items;

    public AiExpirySummary() {}

    public int getTotalProducts() { return totalProducts; }
    public void setTotalProducts(int totalProducts) { this.totalProducts = totalProducts; }

    public int getExpiredCount() { return expiredCount; }
    public void setExpiredCount(int expiredCount) { this.expiredCount = expiredCount; }

    public int getCriticalCount() { return criticalCount; }
    public void setCriticalCount(int criticalCount) { this.criticalCount = criticalCount; }

    public int getWarningCount() { return warningCount; }
    public void setWarningCount(int warningCount) { this.warningCount = warningCount; }

    public int getHealthyCount() { return healthyCount; }
    public void setHealthyCount(int healthyCount) { this.healthyCount = healthyCount; }

    public double getTotalWasteRiskValue() { return totalWasteRiskValue; }
    public void setTotalWasteRiskValue(double totalWasteRiskValue) { this.totalWasteRiskValue = totalWasteRiskValue; }

    public double getTotalSalvageableValue() { return totalSalvageableValue; }
    public void setTotalSalvageableValue(double totalSalvageableValue) { this.totalSalvageableValue = totalSalvageableValue; }

    public List<AiExpiryItem> getItems() { return items; }
    public void setItems(List<AiExpiryItem> items) { this.items = items; }
}

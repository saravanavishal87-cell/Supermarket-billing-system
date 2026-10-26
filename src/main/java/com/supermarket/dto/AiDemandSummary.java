package com.supermarket.dto;

import java.util.List;

public class AiDemandSummary {
    private int totalProducts;
    private int highDemandCount;
    private int stockoutRiskCount;
    private int moderateDemandCount;
    private int lowDemandCount;
    private int totalReorderActions;
    private List<AiDemandItem> items;
    private List<String> executiveInsights;

    public AiDemandSummary() {}

    public int getTotalProducts() { return totalProducts; }
    public void setTotalProducts(int totalProducts) { this.totalProducts = totalProducts; }

    public int getHighDemandCount() { return highDemandCount; }
    public void setHighDemandCount(int highDemandCount) { this.highDemandCount = highDemandCount; }

    public int getStockoutRiskCount() { return stockoutRiskCount; }
    public void setStockoutRiskCount(int stockoutRiskCount) { this.stockoutRiskCount = stockoutRiskCount; }

    public int getModerateDemandCount() { return moderateDemandCount; }
    public void setModerateDemandCount(int moderateDemandCount) { this.moderateDemandCount = moderateDemandCount; }

    public int getLowDemandCount() { return lowDemandCount; }
    public void setLowDemandCount(int lowDemandCount) { this.lowDemandCount = lowDemandCount; }

    public int getTotalReorderActions() { return totalReorderActions; }
    public void setTotalReorderActions(int totalReorderActions) { this.totalReorderActions = totalReorderActions; }

    public List<AiDemandItem> getItems() { return items; }
    public void setItems(List<AiDemandItem> items) { this.items = items; }

    public List<String> getExecutiveInsights() { return executiveInsights; }
    public void setExecutiveInsights(List<String> executiveInsights) { this.executiveInsights = executiveInsights; }
}

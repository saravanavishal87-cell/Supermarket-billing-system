package com.supermarket.dto;

public class MlDemandComparisonItem {
    private int productId;
    private String productName;
    private String categoryName;
    private double price;
    private double stockQty;
    private String unit;
    private double actualPastSales;

    // Predictions
    private double wekaPredictedVelocity;
    private double smilePredictedVelocity;
    private double ensembleVelocity;
    private double daysUntilStockout;
    private double wekaRecommendedOrder;
    private double smileRecommendedOrder;
    private double consensusOrderQty;
    private String demandTier;
    private String modelAgreement; // HIGH, MODERATE, DIVERGENT

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

    public double getActualPastSales() { return actualPastSales; }
    public void setActualPastSales(double actualPastSales) { this.actualPastSales = actualPastSales; }

    public double getWekaPredictedVelocity() { return wekaPredictedVelocity; }
    public void setWekaPredictedVelocity(double wekaPredictedVelocity) { this.wekaPredictedVelocity = wekaPredictedVelocity; }

    public double getSmilePredictedVelocity() { return smilePredictedVelocity; }
    public void setSmilePredictedVelocity(double smilePredictedVelocity) { this.smilePredictedVelocity = smilePredictedVelocity; }

    public double getEnsembleVelocity() { return ensembleVelocity; }
    public void setEnsembleVelocity(double ensembleVelocity) { this.ensembleVelocity = ensembleVelocity; }

    public double getDaysUntilStockout() { return daysUntilStockout; }
    public void setDaysUntilStockout(double daysUntilStockout) { this.daysUntilStockout = daysUntilStockout; }

    public double getWekaRecommendedOrder() { return wekaRecommendedOrder; }
    public void setWekaRecommendedOrder(double wekaRecommendedOrder) { this.wekaRecommendedOrder = wekaRecommendedOrder; }

    public double getSmileRecommendedOrder() { return smileRecommendedOrder; }
    public void setSmileRecommendedOrder(double smileRecommendedOrder) { this.smileRecommendedOrder = smileRecommendedOrder; }

    public double getConsensusOrderQty() { return consensusOrderQty; }
    public void setConsensusOrderQty(double consensusOrderQty) { this.consensusOrderQty = consensusOrderQty; }

    public String getDemandTier() { return demandTier; }
    public void setDemandTier(String demandTier) { this.demandTier = demandTier; }

    public String getModelAgreement() { return modelAgreement; }
    public void setModelAgreement(String modelAgreement) { this.modelAgreement = modelAgreement; }
}

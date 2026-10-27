package com.supermarket.dto;

import java.util.List;

public class MlCustomerCluster {
    private int clusterId;
    private String clusterName;
    private String icon;
    private String color;
    private String description;
    private int customerCount;
    private double percentageOfBase;
    private double avgSpend;
    private double avgFrequency;
    private double avgBasketSize;
    private String marketingStrategy;
    private List<String> sampleCustomers;

    public int getClusterId() { return clusterId; }
    public void setClusterId(int clusterId) { this.clusterId = clusterId; }

    public String getClusterName() { return clusterName; }
    public void setClusterName(String clusterName) { this.clusterName = clusterName; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCustomerCount() { return customerCount; }
    public void setCustomerCount(int customerCount) { this.customerCount = customerCount; }

    public double getPercentageOfBase() { return percentageOfBase; }
    public void setPercentageOfBase(double percentageOfBase) { this.percentageOfBase = percentageOfBase; }

    public double getAvgSpend() { return avgSpend; }
    public void setAvgSpend(double avgSpend) { this.avgSpend = avgSpend; }

    public double getAvgFrequency() { return avgFrequency; }
    public void setAvgFrequency(double avgFrequency) { this.avgFrequency = avgFrequency; }

    public double getAvgBasketSize() { return avgBasketSize; }
    public void setAvgBasketSize(double avgBasketSize) { this.avgBasketSize = avgBasketSize; }

    public String getMarketingStrategy() { return marketingStrategy; }
    public void setMarketingStrategy(String marketingStrategy) { this.marketingStrategy = marketingStrategy; }

    public List<String> getSampleCustomers() { return sampleCustomers; }
    public void setSampleCustomers(List<String> sampleCustomers) { this.sampleCustomers = sampleCustomers; }
}

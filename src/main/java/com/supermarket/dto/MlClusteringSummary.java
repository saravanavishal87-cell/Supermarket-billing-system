package com.supermarket.dto;

import java.util.List;

public class MlClusteringSummary {
    private int totalCustomersAnalyzed;
    private int kClusters;
    private String algorithmUsed;
    private List<MlCustomerCluster> clusters;
    private List<MlCustomerSegmentItem> customers;
    private String executiveSummary;

    public int getTotalCustomersAnalyzed() { return totalCustomersAnalyzed; }
    public void setTotalCustomersAnalyzed(int totalCustomersAnalyzed) { this.totalCustomersAnalyzed = totalCustomersAnalyzed; }

    public int getkClusters() { return kClusters; }
    public void setkClusters(int kClusters) { this.kClusters = kClusters; }

    public String getAlgorithmUsed() { return algorithmUsed; }
    public void setAlgorithmUsed(String algorithmUsed) { this.algorithmUsed = algorithmUsed; }

    public List<MlCustomerCluster> getClusters() { return clusters; }
    public void setClusters(List<MlCustomerCluster> clusters) { this.clusters = clusters; }

    public List<MlCustomerSegmentItem> getCustomers() { return customers; }
    public void setCustomers(List<MlCustomerSegmentItem> customers) { this.customers = customers; }

    public String getExecutiveSummary() { return executiveSummary; }
    public void setExecutiveSummary(String executiveSummary) { this.executiveSummary = executiveSummary; }
}

package com.supermarket.dto;

import java.util.List;
import java.util.Map;

public class MlComparisonReport {
    private String timestamp;
    private Map<String, Object> modelMetadata;
    private List<MlDemandComparisonItem> demandForecasts;
    private List<MlExpiryComparisonItem> expiryClassifications;
    private MlClusteringSummary customerSegmentation;
    private Map<String, Object> performanceMetrics;

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public Map<String, Object> getModelMetadata() { return modelMetadata; }
    public void setModelMetadata(Map<String, Object> modelMetadata) { this.modelMetadata = modelMetadata; }

    public List<MlDemandComparisonItem> getDemandForecasts() { return demandForecasts; }
    public void setDemandForecasts(List<MlDemandComparisonItem> demandForecasts) { this.demandForecasts = demandForecasts; }

    public List<MlExpiryComparisonItem> getExpiryClassifications() { return expiryClassifications; }
    public void setExpiryClassifications(List<MlExpiryComparisonItem> expiryClassifications) { this.expiryClassifications = expiryClassifications; }

    public MlClusteringSummary getCustomerSegmentation() { return customerSegmentation; }
    public void setCustomerSegmentation(MlClusteringSummary customerSegmentation) { this.customerSegmentation = customerSegmentation; }

    public Map<String, Object> getPerformanceMetrics() { return performanceMetrics; }
    public void setPerformanceMetrics(Map<String, Object> performanceMetrics) { this.performanceMetrics = performanceMetrics; }
}

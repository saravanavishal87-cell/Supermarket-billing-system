package com.supermarket.dto;

public class MlCustomerSegmentItem {
    private int customerId;
    private String name;
    private String phone;
    private double totalSpend;
    private int totalBills;
    private double avgBillValue;
    private int daysSinceLastPurchase;
    private int wekaCluster;
    private int smileCluster;
    private String segmentName;
    private String segmentBadgeColor;
    private String suggestedAction;

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public double getTotalSpend() { return totalSpend; }
    public void setTotalSpend(double totalSpend) { this.totalSpend = totalSpend; }

    public int getTotalBills() { return totalBills; }
    public void setTotalBills(int totalBills) { this.totalBills = totalBills; }

    public double getAvgBillValue() { return avgBillValue; }
    public void setAvgBillValue(double avgBillValue) { this.avgBillValue = avgBillValue; }

    public int getDaysSinceLastPurchase() { return daysSinceLastPurchase; }
    public void setDaysSinceLastPurchase(int daysSinceLastPurchase) { this.daysSinceLastPurchase = daysSinceLastPurchase; }

    public int getWekaCluster() { return wekaCluster; }
    public void setWekaCluster(int wekaCluster) { this.wekaCluster = wekaCluster; }

    public int getSmileCluster() { return smileCluster; }
    public void setSmileCluster(int smileCluster) { this.smileCluster = smileCluster; }

    public String getSegmentName() { return segmentName; }
    public void setSegmentName(String segmentName) { this.segmentName = segmentName; }

    public String getSegmentBadgeColor() { return segmentBadgeColor; }
    public void setSegmentBadgeColor(String segmentBadgeColor) { this.segmentBadgeColor = segmentBadgeColor; }

    public String getSuggestedAction() { return suggestedAction; }
    public void setSuggestedAction(String suggestedAction) { this.suggestedAction = suggestedAction; }
}

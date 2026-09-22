package com.supermarket.dto;

public class DashboardStats {
    private double todaySales;
    private int todayBills;
    private int productCount;

    public DashboardStats(double todaySales, int todayBills, int productCount) {
        this.todaySales = todaySales;
        this.todayBills = todayBills;
        this.productCount = productCount;
    }

    public double getTodaySales() { return todaySales; }
    public int getTodayBills() { return todayBills; }
    public int getProductCount() { return productCount; }
}

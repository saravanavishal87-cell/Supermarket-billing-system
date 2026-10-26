package com.supermarket.model;

public class Product {

    private int productId;
    private String barcode;
    private String productName;
    private int categoryId;
    private String categoryName;
    private double price;
    private double stockQty;
    private String unit;
    private String expiryDate;
    private String batchNo;

    public Product() {}

    public Product(int productId, String barcode, String productName,
                   int categoryId, String categoryName, double price,
                   double stockQty, String unit) {
        this(productId, barcode, productName, categoryId, categoryName, price, stockQty, unit, null, null);
    }

    public Product(int productId, String barcode, String productName,
                   int categoryId, String categoryName, double price,
                   double stockQty, String unit, String expiryDate, String batchNo) {
        this.productId = productId;
        this.barcode = barcode;
        this.productName = productName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.price = price;
        this.stockQty = stockQty;
        this.unit = unit;
        this.expiryDate = expiryDate;
        this.batchNo = batchNo;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

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

    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
}

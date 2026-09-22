package com.supermarket.model;

public class Product {

    private int productId;
    private String barcode;
    private String productName;
    private int categoryId;
    private String categoryName;
    private double price;
    private int stockQty;
    private String unit;

    public Product() {}

    public Product(int productId, String barcode, String productName,
                   int categoryId, String categoryName, double price,
                   int stockQty, String unit) {
        this.productId = productId;
        this.barcode = barcode;
        this.productName = productName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.price = price;
        this.stockQty = stockQty;
        this.unit = unit;
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

    public int getStockQty() { return stockQty; }
    public void setStockQty(int stockQty) { this.stockQty = stockQty; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}

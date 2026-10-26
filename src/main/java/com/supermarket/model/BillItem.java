package com.supermarket.model;

public class BillItem {

    private int itemId;
    private int billId;
    private int productId;
    private String productName;
    private String barcode;
    private double quantity;
    private double unitPrice;
    private double lineTotal;

    public BillItem() {}

    public BillItem(Product product, double quantity) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.barcode = product.getBarcode();
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
        this.lineTotal = unitPrice * quantity;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) {
        this.quantity = quantity;
        this.lineTotal = unitPrice * quantity;
    }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        this.lineTotal = unitPrice * quantity;
    }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public double getLineTotal() { return lineTotal; }
    public void setLineTotal(double lineTotal) { this.lineTotal = lineTotal; }
}

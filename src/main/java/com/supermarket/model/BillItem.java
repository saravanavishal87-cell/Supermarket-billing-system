package com.supermarket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BillItem {

    private int itemId;
    private int billId;
    private int productId;
    private String productName;
    private String barcode;
    private double quantity;
    private double unitPrice;
    private double lineTotal;
    private String unit;
    private Double quantityGrams;

    public BillItem() {}

    public BillItem(Product product, double quantity) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.barcode = product.getBarcode();
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
        this.unit = product.getUnit();
        this.lineTotal = Math.round(unitPrice * quantity * 100.0) / 100.0;
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
        if (this.lineTotal == 0 && this.unitPrice > 0) {
            this.lineTotal = Math.round(unitPrice * quantity * 100.0) / 100.0;
        }
    }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        if (this.lineTotal == 0 && this.quantity > 0) {
            this.lineTotal = Math.round(unitPrice * quantity * 100.0) / 100.0;
        }
    }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public double getLineTotal() { return lineTotal; }
    public void setLineTotal(double lineTotal) { this.lineTotal = Math.round(lineTotal * 100.0) / 100.0; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Double getQuantityGrams() { return quantityGrams; }
    public void setQuantityGrams(Double quantityGrams) { this.quantityGrams = quantityGrams; }
}

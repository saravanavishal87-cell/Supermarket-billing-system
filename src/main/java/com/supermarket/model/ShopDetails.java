package com.supermarket.model;

public class ShopDetails {
    private int id = 1;
    private String shopName;
    private String address;
    private String phone;
    private String email;
    private String gstin;
    private String imagePath;
    private int autoDeleteMonths;

    public ShopDetails() {}

    public ShopDetails(int id, String shopName, String address, String phone, String email, String gstin, String imagePath, int autoDeleteMonths) {
        this.id = id;
        this.shopName = shopName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.gstin = gstin;
        this.imagePath = imagePath;
        this.autoDeleteMonths = autoDeleteMonths;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGstin() {
        return gstin;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getAutoDeleteMonths() {
        return autoDeleteMonths;
    }

    public void setAutoDeleteMonths(int autoDeleteMonths) {
        this.autoDeleteMonths = autoDeleteMonths;
    }
}

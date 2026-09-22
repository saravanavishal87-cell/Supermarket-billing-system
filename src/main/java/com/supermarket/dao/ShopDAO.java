package com.supermarket.dao;

import com.supermarket.model.ShopDetails;
import com.supermarket.util.DatabaseConnection;

import java.sql.*;

public class ShopDAO {

    public ShopDetails getShopDetails() throws SQLException {
        String sql = "SELECT id, shop_name, address, phone, email, gstin, image_path, auto_delete_months FROM shop_details WHERE id = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                ShopDetails shop = new ShopDetails();
                shop.setId(rs.getInt("id"));
                shop.setShopName(rs.getString("shop_name"));
                shop.setAddress(rs.getString("address"));
                shop.setPhone(rs.getString("phone"));
                shop.setEmail(rs.getString("email"));
                shop.setGstin(rs.getString("gstin"));
                shop.setImagePath(rs.getString("image_path"));
                shop.setAutoDeleteMonths(rs.getInt("auto_delete_months"));
                return shop;
            }
        }
        // Fallback default
        return new ShopDetails(1, "RAHUL STORES", "123 Main Bazaar Road, Market City", "+91 98765 43210", "contact@rahulstores.com", "33AAAAA0000A1Z5", "/images/shop_banner.jpg", 3);
    }

    public boolean updateShopDetails(ShopDetails shop) throws SQLException {
        String sql = """
            MERGE INTO shop_details (id, shop_name, address, phone, email, gstin, image_path, auto_delete_months) KEY(id)
            VALUES (1, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, shop.getShopName());
            ps.setString(2, shop.getAddress());
            ps.setString(3, shop.getPhone());
            ps.setString(4, shop.getEmail());
            ps.setString(5, shop.getGstin());
            ps.setString(6, shop.getImagePath());
            ps.setInt(7, shop.getAutoDeleteMonths());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // MySQL fallback syntax if MERGE is not supported
            String mysqlSql = """
                INSERT INTO shop_details (id, shop_name, address, phone, email, gstin, image_path, auto_delete_months)
                VALUES (1, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                shop_name=VALUES(shop_name), address=VALUES(address), phone=VALUES(phone),
                email=VALUES(email), gstin=VALUES(gstin), image_path=VALUES(image_path),
                auto_delete_months=VALUES(auto_delete_months)
                """;
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(mysqlSql)) {
                ps.setString(1, shop.getShopName());
                ps.setString(2, shop.getAddress());
                ps.setString(3, shop.getPhone());
                ps.setString(4, shop.getEmail());
                ps.setString(5, shop.getGstin());
                ps.setString(6, shop.getImagePath());
                ps.setInt(7, shop.getAutoDeleteMonths());
                return ps.executeUpdate() > 0;
            }
        }
    }

    public boolean updateImagePath(String imagePath) throws SQLException {
        String sql = "UPDATE shop_details SET image_path = ? WHERE id = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, imagePath);
            return ps.executeUpdate() > 0;
        }
    }
}

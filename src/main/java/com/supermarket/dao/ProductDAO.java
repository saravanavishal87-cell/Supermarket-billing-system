package com.supermarket.dao;

import com.supermarket.model.Product;
import com.supermarket.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = """
            SELECT p.product_id, p.barcode, p.product_name, p.category_id,
                   c.category_name, p.price, p.stock_qty, p.unit
            FROM products p
            LEFT JOIN categories c ON p.category_id = c.category_id
            ORDER BY p.product_name
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                products.add(mapRow(rs));
            }
        }
        return products;
    }

    public Product findById(int productId) throws SQLException {
        String sql = """
            SELECT p.product_id, p.barcode, p.product_name, p.category_id,
                   c.category_name, p.price, p.stock_qty, p.unit
            FROM products p
            LEFT JOIN categories c ON p.category_id = c.category_id
            WHERE p.product_id = ?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public Product findByBarcode(String barcode) throws SQLException {
        String sql = """
            SELECT p.product_id, p.barcode, p.product_name, p.category_id,
                   c.category_name, p.price, p.stock_qty, p.unit
            FROM products p
            LEFT JOIN categories c ON p.category_id = c.category_id
            WHERE p.barcode = ?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, barcode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Product> searchByName(String keyword) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = """
            SELECT p.product_id, p.barcode, p.product_name, p.category_id,
                   c.category_name, p.price, p.stock_qty, p.unit
            FROM products p
            LEFT JOIN categories c ON p.category_id = c.category_id
            WHERE p.product_name LIKE ? OR p.barcode LIKE ? OR CAST(p.product_id AS CHAR) = ?
            ORDER BY p.product_name
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, keyword.replaceAll("^#", ""));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) products.add(mapRow(rs));
            }
        }
        return products;
    }

    public void addProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products (barcode, product_name, category_id, price, stock_qty, unit) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getBarcode());
            ps.setString(2, product.getProductName());
            ps.setInt(3, product.getCategoryId());
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, product.getStockQty());
            ps.setString(6, product.getUnit());
            ps.executeUpdate();
        }
    }

    public void updateProduct(Product product) throws SQLException {
        String sql = "UPDATE products SET barcode=?, product_name=?, category_id=?, price=?, stock_qty=?, unit=? WHERE product_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getBarcode());
            ps.setString(2, product.getProductName());
            ps.setInt(3, product.getCategoryId());
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, product.getStockQty());
            ps.setString(6, product.getUnit());
            ps.setInt(7, product.getProductId());
            ps.executeUpdate();
        }
    }

    public void deleteProduct(int productId) throws SQLException {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }
    }

    public int getProductCount() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products")) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        return new Product(
            rs.getInt("product_id"),
            rs.getString("barcode"),
            rs.getString("product_name"),
            rs.getInt("category_id"),
            rs.getString("category_name"),
            rs.getDouble("price"),
            rs.getInt("stock_qty"),
            rs.getString("unit")
        );
    }
}

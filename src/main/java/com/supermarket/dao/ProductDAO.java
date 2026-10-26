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
                   c.category_name, p.price, p.stock_qty, p.unit, p.expiry_date, p.batch_no
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
                   c.category_name, p.price, p.stock_qty, p.unit, p.expiry_date, p.batch_no
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
                   c.category_name, p.price, p.stock_qty, p.unit, p.expiry_date, p.batch_no
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
                   c.category_name, p.price, p.stock_qty, p.unit, p.expiry_date, p.batch_no
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
        boolean hasCustomId = product.getProductId() > 0;
        String sql = hasCustomId
            ? "INSERT INTO products (product_id, barcode, product_name, category_id, price, stock_qty, unit, expiry_date, batch_no) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            : "INSERT INTO products (barcode, product_name, category_id, price, stock_qty, unit, expiry_date, batch_no) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int idx = 1;
            if (hasCustomId) {
                ps.setInt(idx++, product.getProductId());
            }
            String barcode = (product.getBarcode() != null && !product.getBarcode().trim().isEmpty())
                ? product.getBarcode().trim() : null;
            if (barcode != null) {
                ps.setString(idx++, barcode);
            } else {
                ps.setNull(idx++, java.sql.Types.VARCHAR);
            }
            ps.setString(idx++, product.getProductName());
            ps.setInt(idx++, product.getCategoryId());
            ps.setDouble(idx++, product.getPrice());
            ps.setDouble(idx++, product.getStockQty());
            ps.setString(idx++, product.getUnit());
            
            // Expiry Date
            if (product.getExpiryDate() != null && !product.getExpiryDate().trim().isEmpty()) {
                try {
                    ps.setDate(idx++, java.sql.Date.valueOf(product.getExpiryDate().trim()));
                } catch (IllegalArgumentException e) {
                    ps.setNull(idx++, java.sql.Types.DATE);
                }
            } else {
                ps.setNull(idx++, java.sql.Types.DATE);
            }
            // Batch No
            ps.setString(idx++, (product.getBatchNo() != null && !product.getBatchNo().trim().isEmpty()) ? product.getBatchNo().trim() : null);

            ps.executeUpdate();
            if (!hasCustomId) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        product.setProductId(keys.getInt(1));
                    }
                }
            }
        }
    }

    public void updateProduct(Product product) throws SQLException {
        updateProduct(product.getProductId(), product);
    }

    public void updateProduct(int oldId, Product product) throws SQLException {
        int newId = product.getProductId() > 0 ? product.getProductId() : oldId;
        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            DatabaseConnection.disableForeignKeyChecks(conn);
            try {
                String sql = "UPDATE products SET product_id=?, barcode=?, product_name=?, category_id=?, price=?, stock_qty=?, unit=?, expiry_date=?, batch_no=? WHERE product_id=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, newId);
                    String barcode = (product.getBarcode() != null && !product.getBarcode().trim().isEmpty())
                        ? product.getBarcode().trim() : null;
                    if (barcode != null) {
                        ps.setString(2, barcode);
                    } else {
                        ps.setNull(2, java.sql.Types.VARCHAR);
                    }
                    ps.setString(3, product.getProductName());
                    ps.setInt(4, product.getCategoryId());
                    ps.setDouble(5, product.getPrice());
                    ps.setDouble(6, product.getStockQty());
                    ps.setString(7, product.getUnit());
                    
                    if (product.getExpiryDate() != null && !product.getExpiryDate().trim().isEmpty()) {
                        try {
                            ps.setDate(8, java.sql.Date.valueOf(product.getExpiryDate().trim()));
                        } catch (IllegalArgumentException e) {
                            ps.setNull(8, java.sql.Types.DATE);
                        }
                    } else {
                        ps.setNull(8, java.sql.Types.DATE);
                    }
                    ps.setString(9, (product.getBatchNo() != null && !product.getBatchNo().trim().isEmpty()) ? product.getBatchNo().trim() : null);
                    ps.setInt(10, oldId);
                    ps.executeUpdate();
                }
                if (newId != oldId) {
                    String updateItemsSql = "UPDATE bill_items SET product_id = ? WHERE product_id = ?";
                    try (PreparedStatement ps = conn.prepareStatement(updateItemsSql)) {
                        ps.setInt(1, newId);
                        ps.setInt(2, oldId);
                        ps.executeUpdate();
                    }
                }
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                DatabaseConnection.enableForeignKeyChecks(conn);
                conn.setAutoCommit(autoCommit);
            }
        }
    }

    public void updatePrice(int productId, double newPrice) throws SQLException {
        String sql = "UPDATE products SET price = ? WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newPrice);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }

    public void updateStock(int productId, double addedStock) throws SQLException {
        String sql = "UPDATE products SET stock_qty = stock_qty + ? WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, addedStock);
            ps.setInt(2, productId);
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
        String exp = null;
        try {
            Date d = rs.getDate("expiry_date");
            if (d != null) exp = d.toString();
        } catch (SQLException ignored) {
            try { exp = rs.getString("expiry_date"); } catch (SQLException ignored2) {}
        }
        String batch = null;
        try {
            batch = rs.getString("batch_no");
        } catch (SQLException ignored) {}

        return new Product(
            rs.getInt("product_id"),
            rs.getString("barcode"),
            rs.getString("product_name"),
            rs.getInt("category_id"),
            rs.getString("category_name"),
            rs.getDouble("price"),
            rs.getDouble("stock_qty"),
            rs.getString("unit"),
            exp,
            batch
        );
    }
}

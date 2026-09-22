package com.supermarket.dao;

import com.supermarket.model.Bill;
import com.supermarket.model.BillItem;
import com.supermarket.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BillDAO {

    public String saveBill(Bill bill) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            String billNumber = generateBillNumber();
            bill.setBillNumber(billNumber);

            String billSql = """
                INSERT INTO bills (bill_number, cashier_id, customer_name, customer_phone, customer_id, subtotal, discount, tax_amount, grand_total, payment_mode)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
            int billId;
            try (PreparedStatement ps = conn.prepareStatement(billSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, bill.getBillNumber());
                ps.setInt(2, bill.getCashierId());
                ps.setString(3, bill.getCustomerName());
                ps.setString(4, bill.getCustomerPhone());
                if (bill.getCustomerId() != null && bill.getCustomerId() > 0) {
                    ps.setInt(5, bill.getCustomerId());
                } else {
                    ps.setNull(5, Types.INTEGER);
                }
                ps.setDouble(6, bill.getSubtotal());
                ps.setDouble(7, bill.getDiscount());
                ps.setDouble(8, bill.getTaxAmount());
                ps.setDouble(9, bill.getGrandTotal());
                ps.setString(10, bill.getPaymentMode());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("Failed to get bill ID");
                    billId = keys.getInt(1);
                }
            }

            String itemSql = "INSERT INTO bill_items (bill_id, product_id, quantity, unit_price, line_total) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(itemSql)) {
                for (BillItem item : bill.getItems()) {
                    ps.setInt(1, billId);
                    ps.setInt(2, item.getProductId());
                    ps.setInt(3, item.getQuantity());
                    ps.setDouble(4, item.getUnitPrice());
                    ps.setDouble(5, item.getLineTotal());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            String stockSql = "UPDATE products SET stock_qty = stock_qty - ? WHERE product_id = ? AND stock_qty >= ?";
            try (PreparedStatement ps = conn.prepareStatement(stockSql)) {
                for (BillItem item : bill.getItems()) {
                    ps.setInt(1, item.getQuantity());
                    ps.setInt(2, item.getProductId());
                    ps.setInt(3, item.getQuantity());
                    if (ps.executeUpdate() == 0) {
                        throw new SQLException("Insufficient stock for product ID: " + item.getProductId());
                    }
                }
            }

            conn.commit();
            bill.setBillId(billId);
            return billNumber;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    public double getTodaySales() throws SQLException {
        String sql = "SELECT COALESCE(SUM(grand_total), 0) FROM bills WHERE DATE(bill_date) = CURDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }

    public int getTodayBillCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM bills WHERE DATE(bill_date) = CURDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int deleteBillsOlderThanMonths(int months) throws SQLException {
        if (months <= 0) return 0;
        Connection conn = DatabaseConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            
            // Delete bill items for bills older than N months using Java Timestamp calculation for database compatibility
            java.sql.Timestamp cutoff = java.sql.Timestamp.valueOf(java.time.LocalDateTime.now().minusMonths(months));
            
            String deleteItemsSql = "DELETE FROM bill_items WHERE bill_id IN (SELECT bill_id FROM bills WHERE bill_date < ?)";
            try (PreparedStatement ps = conn.prepareStatement(deleteItemsSql)) {
                ps.setTimestamp(1, cutoff);
                ps.executeUpdate();
            }

            String deleteBillsSql = "DELETE FROM bills WHERE bill_date < ?";
            int deletedCount = 0;
            try (PreparedStatement ps = conn.prepareStatement(deleteBillsSql)) {
                ps.setTimestamp(1, cutoff);
                deletedCount = ps.executeUpdate();
            }

            conn.commit();
            return deletedCount;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    public int getPurgeCandidateCount(int months) throws SQLException {
        if (months <= 0) return 0;
        java.sql.Timestamp cutoff = java.sql.Timestamp.valueOf(java.time.LocalDateTime.now().minusMonths(months));
        String sql = "SELECT COUNT(*) FROM bills WHERE bill_date < ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, cutoff);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public java.util.List<Bill> getBillsByCustomer(String search) throws SQLException {
        java.util.List<Bill> list = new java.util.ArrayList<>();
        String sql = "SELECT bill_id, bill_number, cashier_id, customer_name, customer_phone, customer_id, subtotal, discount, tax_amount, grand_total, payment_mode, bill_date FROM bills";
        if (search != null && !search.trim().isEmpty()) {
            sql += " WHERE LOWER(customer_name) LIKE ? OR customer_phone LIKE ? OR CAST(customer_id AS CHAR) = ? OR bill_number LIKE ? OR CAST(bill_id AS CHAR) = ?";
        }
        sql += " ORDER BY bill_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (search != null && !search.trim().isEmpty()) {
                String q = search.trim();
                String wildcard = "%" + q.toLowerCase() + "%";
                ps.setString(1, wildcard);
                ps.setString(2, wildcard);
                ps.setString(3, q);
                ps.setString(4, wildcard);
                ps.setString(5, q);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bill bill = mapBill(conn, rs);
                    list.add(bill);
                }
            }
        }
        return list;
    }

    public java.util.List<Bill> getBillsByCustomerRef(Integer customerId, String phone) throws SQLException {
        java.util.List<Bill> list = new java.util.ArrayList<>();
        String sql = "SELECT bill_id, bill_number, cashier_id, customer_name, customer_phone, customer_id, subtotal, discount, tax_amount, grand_total, payment_mode, bill_date FROM bills WHERE ";
        boolean hasId = (customerId != null && customerId > 0);
        boolean hasPhone = (phone != null && !phone.trim().isEmpty());

        if (hasId && hasPhone) {
            sql += "customer_id = ? OR customer_phone = ?";
        } else if (hasId) {
            sql += "customer_id = ?";
        } else if (hasPhone) {
            sql += "customer_phone = ?";
        } else {
            return list;
        }
        sql += " ORDER BY bill_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (hasId && hasPhone) {
                ps.setInt(1, customerId);
                ps.setString(2, phone.trim());
            } else if (hasId) {
                ps.setInt(1, customerId);
            } else {
                ps.setString(1, phone.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bill bill = mapBill(conn, rs);
                    list.add(bill);
                }
            }
        }
        return list;
    }

    public boolean updateBillCustomer(String billIdentifier, int customerId, String customerPhone, String customerName) throws SQLException {
        String sql = "UPDATE bills SET customer_id = ?, customer_phone = ?, customer_name = ? WHERE bill_number = ? OR CAST(bill_id AS CHAR) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setString(2, customerPhone);
            ps.setString(3, customerName);
            ps.setString(4, billIdentifier.trim());
            ps.setString(5, billIdentifier.trim());
            return ps.executeUpdate() > 0;
        }
    }

    private Bill mapBill(Connection conn, ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setBillId(rs.getInt("bill_id"));
        bill.setBillNumber(rs.getString("bill_number"));
        bill.setCashierId(rs.getInt("cashier_id"));
        bill.setCustomerName(rs.getString("customer_name"));
        bill.setCustomerPhone(rs.getString("customer_phone"));
        Object custIdObj = rs.getObject("customer_id");
        if (custIdObj != null) {
            bill.setCustomerId(((Number) custIdObj).intValue());
        }
        
        // Fetch bill items first
        bill.setItems(getBillItems(conn, bill.getBillId()));

        // Set exact database totals (avoiding recalculate overwrite)
        bill.setDiscount(rs.getDouble("discount"));
        bill.setSubtotal(rs.getDouble("subtotal"));
        bill.setTaxAmount(rs.getDouble("tax_amount"));
        bill.setGrandTotal(rs.getDouble("grand_total"));
        bill.setPaymentMode(rs.getString("payment_mode"));
        bill.setBillDate(rs.getTimestamp("bill_date"));
        return bill;
    }

    private java.util.List<BillItem> getBillItems(Connection conn, int billId) throws SQLException {
        java.util.List<BillItem> items = new java.util.ArrayList<>();
        String sql = """
            SELECT bi.item_id, bi.bill_id, bi.product_id, bi.quantity, bi.unit_price, bi.line_total, p.product_name
            FROM bill_items bi
            LEFT JOIN products p ON bi.product_id = p.product_id
            WHERE bi.bill_id = ?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BillItem item = new BillItem();
                    item.setItemId(rs.getInt("item_id"));
                    item.setBillId(rs.getInt("bill_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getDouble("unit_price"));
                    item.setLineTotal(rs.getDouble("line_total"));
                    item.setProductName(rs.getString("product_name"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    public java.util.Map<String, Object> getMonthlyBillStats(int year, int month) throws SQLException {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1);

        String sql = """
            SELECT COUNT(*) AS bill_count,
                   COALESCE(SUM(grand_total), 0) AS total_sales,
                   COALESCE(SUM(tax_amount), 0) AS total_tax,
                   COALESCE(SUM(subtotal), 0) AS total_subtotal,
                   COALESCE(SUM(discount), 0) AS total_discount
            FROM bills
            WHERE bill_date >= ? AND bill_date < ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(start.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(end.atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    map.put("billCount", rs.getInt("bill_count"));
                    map.put("totalSales", rs.getDouble("total_sales"));
                    map.put("totalTax", rs.getDouble("total_tax"));
                    map.put("totalSubtotal", rs.getDouble("total_subtotal"));
                    map.put("totalDiscount", rs.getDouble("total_discount"));
                }
            }
        }
        return map;
    }

    public java.util.List<Bill> getBillsByMonth(int year, int month) throws SQLException {
        java.util.List<Bill> list = new java.util.ArrayList<>();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1);

        String sql = """
            SELECT bill_id, bill_number, cashier_id, customer_name, customer_phone, customer_id, 
                   subtotal, discount, tax_amount, grand_total, payment_mode, bill_date
            FROM bills
            WHERE bill_date >= ? AND bill_date < ?
            ORDER BY bill_date DESC, bill_id DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(start.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(end.atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapBill(conn, rs));
                }
            }
        }
        return list;
    }

    private String generateBillNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = (int) (Math.random() * 9000) + 1000;
        return "INV-" + date + "-" + random;
    }
}

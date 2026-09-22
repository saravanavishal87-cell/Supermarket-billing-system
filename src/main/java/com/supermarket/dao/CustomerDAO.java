package com.supermarket.dao;

import com.supermarket.model.Customer;
import com.supermarket.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public List<Customer> getAllCustomers(String search) throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT customer_id, name, phone, email, address, created_at FROM customers";
        if (search != null && !search.trim().isEmpty()) {
            sql += " WHERE LOWER(name) LIKE ? OR phone LIKE ?";
        }
        sql += " ORDER BY customer_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (search != null && !search.trim().isEmpty()) {
                String keyword = "%" + search.trim().toLowerCase() + "%";
                ps.setString(1, keyword);
                ps.setString(2, keyword);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Customer c = mapRow(rs);
                    list.add(c);
                }
            }
        }
        return list;
    }

    public Customer getById(int id) throws SQLException {
        String sql = "SELECT customer_id, name, phone, email, address, created_at FROM customers WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Customer getByPhone(String phone) throws SQLException {
        String sql = "SELECT customer_id, name, phone, email, address, created_at FROM customers WHERE phone = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Customer getByIdOrPhone(String idOrPhone) throws SQLException {
        if (idOrPhone == null || idOrPhone.trim().isEmpty()) return null;
        String trimmed = idOrPhone.trim();
        try {
            int id = Integer.parseInt(trimmed);
            Customer c = getById(id);
            if (c != null) return c;
        } catch (NumberFormatException ignored) {}

        return getByPhone(trimmed);
    }

    public boolean addCustomer(Customer c) throws SQLException {
        String sql = "INSERT INTO customers (name, phone, email, address) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getAddress());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        c.setCustomerId(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean updateCustomer(Customer c) throws SQLException {
        String sql = "UPDATE customers SET name = ?, phone = ?, email = ?, address = ? WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getAddress());
            ps.setInt(5, c.getCustomerId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteCustomer(int id) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getInt("customer_id"));
        c.setName(rs.getString("name"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        c.setAddress(rs.getString("address"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }
}

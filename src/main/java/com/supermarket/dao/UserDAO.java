package com.supermarket.dao;

import com.supermarket.model.User;
import com.supermarket.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT user_id, username, password, full_name, role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT user_id, username, password, full_name, role FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT user_id, username, password, full_name, role FROM users WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public java.util.List<User> getCashiers() throws SQLException {
        java.util.List<User> list = new java.util.ArrayList<>();
        String sql = "SELECT user_id, username, password, full_name, role FROM users WHERE role = 'CASHIER' ORDER BY user_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean addCashier(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, full_name, role) VALUES (?, ?, ?, 'CASHIER')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername().trim());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName().trim());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        user.setUserId(keys.getInt(1));
                    }
                }
                user.setRole("CASHIER");
                return true;
            }
        }
        return false;
    }

    public boolean updateUser(int userId, String fullName, String username, String newPassword) throws SQLException {
        boolean updatePass = (newPassword != null && !newPassword.isBlank());
        String sql = updatePass
                ? "UPDATE users SET full_name = ?, username = ?, password = ? WHERE user_id = ?"
                : "UPDATE users SET full_name = ?, username = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName.trim());
            ps.setString(2, username.trim());
            if (updatePass) {
                ps.setString(3, newPassword);
                ps.setInt(4, userId);
            } else {
                ps.setInt(3, userId);
            }
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteUser(int userId) throws SQLException {
        // Safety: ensure cannot delete admin with ID 1 or role ADMIN
        String checkSql = "SELECT role FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    if ("ADMIN".equalsIgnoreCase(role) || userId == 1) {
                        return false;
                    }
                } else {
                    return false;
                }
            }

            String deleteSql = "DELETE FROM users WHERE user_id = ?";
            try (PreparedStatement delPs = conn.prepareStatement(deleteSql)) {
                delPs.setInt(1, userId);
                return delPs.executeUpdate() > 0;
            }
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("full_name"),
            rs.getString("role")
        );
    }
}

package com.supermarket.dao;

import com.supermarket.model.Expense;
import com.supermarket.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseDAO {

    public boolean addExpense(Expense expense) throws SQLException {
        String sql = """
            INSERT INTO expenses (expense_date, category, amount, description, payment_mode, recorded_by)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, expense.getExpenseDate());
            ps.setString(2, expense.getCategory());
            ps.setDouble(3, expense.getAmount());
            ps.setString(4, expense.getDescription() != null ? expense.getDescription().trim() : null);
            ps.setString(5, expense.getPaymentMode() != null ? expense.getPaymentMode() : "CASH");
            if (expense.getRecordedBy() != null && expense.getRecordedBy() > 0) {
                ps.setInt(6, expense.getRecordedBy());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        expense.setExpenseId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public List<Expense> getExpensesByMonth(int year, int month) throws SQLException {
        List<Expense> list = new ArrayList<>();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1);

        String sql = """
            SELECT e.expense_id, e.expense_date, e.category, e.amount, e.description, 
                   e.payment_mode, e.recorded_by, u.full_name AS recorded_by_name, e.created_at
            FROM expenses e
            LEFT JOIN users u ON e.recorded_by = u.user_id
            WHERE e.expense_date >= ? AND e.expense_date < ?
            ORDER BY e.expense_date DESC, e.expense_id DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public boolean deleteExpense(int expenseId) throws SQLException {
        String sql = "DELETE FROM expenses WHERE expense_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, expenseId);
            return ps.executeUpdate() > 0;
        }
    }

    public Map<String, Double> getExpenseSummaryByMonth(int year, int month) throws SQLException {
        Map<String, Double> summary = new HashMap<>();
        summary.put("total", 0.0);
        summary.put("rent", 0.0);
        summary.put("electric", 0.0);
        summary.put("purchase", 0.0);
        summary.put("salary", 0.0);
        summary.put("other", 0.0);

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1);

        String sql = """
            SELECT category, COALESCE(SUM(amount), 0) AS total_cat
            FROM expenses
            WHERE expense_date >= ? AND expense_date < ?
            GROUP BY category
            """;

        double total = 0.0;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String cat = rs.getString("category");
                    double amt = rs.getDouble("total_cat");
                    total += amt;

                    if (cat == null) cat = "Other";
                    String catLower = cat.trim().toLowerCase();
                    if (catLower.contains("rent")) {
                        summary.put("rent", summary.getOrDefault("rent", 0.0) + amt);
                    } else if (catLower.contains("electric") || catLower.contains("power") || catLower.contains("utility")) {
                        summary.put("electric", summary.getOrDefault("electric", 0.0) + amt);
                    } else if (catLower.contains("purchase") || catLower.contains("stock") || catLower.contains("inventory")) {
                        summary.put("purchase", summary.getOrDefault("purchase", 0.0) + amt);
                    } else if (catLower.contains("salary") || catLower.contains("wage")) {
                        summary.put("salary", summary.getOrDefault("salary", 0.0) + amt);
                    } else {
                        summary.put("other", summary.getOrDefault("other", 0.0) + amt);
                    }
                }
            }
        }
        summary.put("total", total);
        return summary;
    }

    private Expense mapRow(ResultSet rs) throws SQLException {
        return new Expense(
            rs.getInt("expense_id"),
            rs.getDate("expense_date"),
            rs.getString("category"),
            rs.getDouble("amount"),
            rs.getString("description"),
            rs.getString("payment_mode"),
            (Integer) rs.getObject("recorded_by"),
            rs.getString("recorded_by_name"),
            rs.getTimestamp("created_at")
        );
    }
}

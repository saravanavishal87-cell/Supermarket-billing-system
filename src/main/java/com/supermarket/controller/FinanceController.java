package com.supermarket.controller;

import com.supermarket.dao.BillDAO;
import com.supermarket.dao.ExpenseDAO;
import com.supermarket.model.Bill;
import com.supermarket.model.Expense;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
public class FinanceController {

    private final ExpenseDAO expenseDAO = new ExpenseDAO();
    private final BillDAO billDAO = new BillDAO();

    @GetMapping("/monthly")
    public ResponseEntity<?> getMonthlyFinance(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month) {
        try {
            LocalDate now = LocalDate.now();
            int queryYear = (year != null && year >= 2000 && year <= 2100) ? year : now.getYear();
            int queryMonth = (month != null && month >= 1 && month <= 12) ? month : now.getMonthValue();

            Map<String, Object> billStats = billDAO.getMonthlyBillStats(queryYear, queryMonth);
            List<Bill> bills = billDAO.getBillsByMonth(queryYear, queryMonth);

            Map<String, Double> expenseStats = expenseDAO.getExpenseSummaryByMonth(queryYear, queryMonth);
            List<Expense> expenses = expenseDAO.getExpensesByMonth(queryYear, queryMonth);

            double totalSales = ((Number) billStats.getOrDefault("totalSales", 0.0)).doubleValue();
            double totalTax = ((Number) billStats.getOrDefault("totalTax", 0.0)).doubleValue();
            double totalSubtotal = ((Number) billStats.getOrDefault("totalSubtotal", 0.0)).doubleValue();
            double totalDiscount = ((Number) billStats.getOrDefault("totalDiscount", 0.0)).doubleValue();
            int billCount = ((Number) billStats.getOrDefault("billCount", 0)).intValue();

            double totalExpenses = expenseStats.getOrDefault("total", 0.0);
            double rentExpenses = expenseStats.getOrDefault("rent", 0.0);
            double electricExpenses = expenseStats.getOrDefault("electric", 0.0);
            double purchaseExpenses = expenseStats.getOrDefault("purchase", 0.0);
            double salaryExpenses = expenseStats.getOrDefault("salary", 0.0);
            double otherExpenses = expenseStats.getOrDefault("other", 0.0);

            double netProfitLoss = totalSales - totalExpenses;
            boolean isProfit = netProfitLoss >= 0;
            double profitMargin = totalSales > 0 ? (netProfitLoss / totalSales) * 100.0 : 0.0;

            Map<String, Object> response = new HashMap<>();
            response.put("year", queryYear);
            response.put("month", queryMonth);

            // Income / Revenue Summary
            response.put("totalSales", totalSales);
            response.put("totalTax", totalTax);
            response.put("totalSubtotal", totalSubtotal);
            response.put("totalDiscount", totalDiscount);
            response.put("billCount", billCount);

            // Expense Summary
            response.put("totalExpenses", totalExpenses);
            response.put("rentExpenses", rentExpenses);
            response.put("electricExpenses", electricExpenses);
            response.put("purchaseExpenses", purchaseExpenses);
            response.put("salaryExpenses", salaryExpenses);
            response.put("otherExpenses", otherExpenses);

            // Profit & Loss
            response.put("netProfitLoss", netProfitLoss);
            response.put("isProfit", isProfit);
            response.put("profitMargin", Math.round(profitMargin * 100.0) / 100.0);

            // Detailed lists
            response.put("bills", bills);
            response.put("expenses", expenses);

            return ResponseEntity.ok(response);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Database error calculating monthly finance: " + e.getMessage()));
        }
    }

    @PostMapping("/expenses")
    public ResponseEntity<?> addExpense(@RequestBody Map<String, Object> payload) {
        try {
            String category = (String) payload.get("category");
            Object amountObj = payload.get("amount");
            String description = (String) payload.get("description");
            String dateStr = (String) payload.get("expenseDate");
            String paymentMode = (String) payload.getOrDefault("paymentMode", "CASH");
            Object recordedByObj = payload.get("recordedBy");

            if (category == null || category.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Expense category is required."));
            }

            if (amountObj == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Expense amount is required."));
            }
            double amount = Double.parseDouble(amountObj.toString());
            if (amount <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Expense amount must be greater than 0."));
            }

            // Enforce mandatory reason/description when category is 'Other'
            if ("other".equalsIgnoreCase(category.trim())) {
                if (description == null || description.trim().isBlank()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Please provide a specific reason / description when selecting category 'Other'."));
                }
            }

            Date expenseDate;
            if (dateStr != null && !dateStr.isBlank()) {
                expenseDate = Date.valueOf(dateStr);
            } else {
                expenseDate = Date.valueOf(LocalDate.now());
            }

            Integer recordedBy = null;
            if (recordedByObj != null) {
                recordedBy = Integer.parseInt(recordedByObj.toString());
            }

            Expense exp = new Expense();
            exp.setExpenseDate(expenseDate);
            exp.setCategory(category.trim());
            exp.setAmount(amount);
            exp.setDescription(description != null ? description.trim() : null);
            exp.setPaymentMode(paymentMode);
            exp.setRecordedBy(recordedBy);

            boolean saved = expenseDAO.addExpense(exp);
            if (saved) {
                return ResponseEntity.status(HttpStatus.CREATED).body(exp);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to save expense."));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid format: " + e.getMessage()));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Database error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable("id") int id) {
        try {
            boolean deleted = expenseDAO.deleteExpense(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Expense deleted successfully."));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Expense not found."));
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Database error: " + e.getMessage()));
        }
    }
}

package com.supermarket.controller;

import com.supermarket.dao.BillDAO;
import com.supermarket.dao.ProductDAO;
import com.supermarket.dto.DashboardStats;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final BillDAO billDAO = new BillDAO();
    private final ProductDAO productDAO = new ProductDAO();

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            return ResponseEntity.ok(new DashboardStats(
                billDAO.getTodaySales(),
                billDAO.getTodayBillCount(),
                productDAO.getProductCount()
            ));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
}

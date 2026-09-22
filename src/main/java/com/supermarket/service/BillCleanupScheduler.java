package com.supermarket.service;

import com.supermarket.dao.BillDAO;
import com.supermarket.dao.ShopDAO;
import com.supermarket.model.ShopDetails;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class BillCleanupScheduler {

    private final ShopDAO shopDAO = new ShopDAO();
    private final BillDAO billDAO = new BillDAO();

    // Run every day at 2:00 AM
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldPurchaseDetails() {
        try {
            ShopDetails shop = shopDAO.getShopDetails();
            int retentionMonths = shop.getAutoDeleteMonths();
            if (retentionMonths > 0) {
                int deleted = billDAO.deleteBillsOlderThanMonths(retentionMonths);
                System.out.println("[BillCleanupScheduler] Automatically purged " + deleted + " bill records older than " + retentionMonths + " months.");
            }
        } catch (SQLException e) {
            System.err.println("[BillCleanupScheduler] Error during automatic purchase history cleanup: " + e.getMessage());
        }
    }
}

package com.supermarket.controller;

import com.supermarket.dao.BillDAO;
import com.supermarket.dao.ShopDAO;
import com.supermarket.model.ShopDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

    private final ShopDAO shopDAO = new ShopDAO();
    private final BillDAO billDAO = new BillDAO();

    @GetMapping
    public ResponseEntity<?> getShopDetails() {
        try {
            ShopDetails shop = shopDAO.getShopDetails();
            return ResponseEntity.ok(shop);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateShopDetails(@RequestBody ShopDetails shop) {
        if (shop.getShopName() == null || shop.getShopName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Shop name cannot be empty"));
        }
        try {
            shop.setId(1);
            boolean updated = shopDAO.updateShopDetails(shop);
            if (updated) {
                return ResponseEntity.ok(shop);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Failed to update shop details"));
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/image")
    public ResponseEntity<?> uploadShopImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Please select a file to upload"));
        }

        try {
            // Save inside src/main/resources/static/images and target/classes/static/images if exists
            String filename = "shop_logo_" + System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9.-]", "_");
            String uploadDirStr = "src/main/resources/static/images";
            Path uploadDirPath = Paths.get(uploadDirStr);
            if (!Files.exists(uploadDirPath)) {
                Files.createDirectories(uploadDirPath);
            }

            Path targetPath = uploadDirPath.resolve(filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Also copy to target folder for immediate runtime availability
            Path targetBuildDir = Paths.get("target/classes/static/images");
            if (Files.exists(targetBuildDir)) {
                Files.copy(file.getInputStream(), targetBuildDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            }

            String relativePath = "/images/" + filename;
            shopDAO.updateImagePath(relativePath);

            return ResponseEntity.ok(Map.of(
                "message", "Shop image uploaded successfully",
                "imagePath", relativePath
            ));
        } catch (IOException | SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to save uploaded image: " + e.getMessage()));
        }
    }

    @PostMapping("/purge-purchases")
    public ResponseEntity<?> purgeOldBills(@RequestParam(value = "months", required = false) Integer months) {
        try {
            ShopDetails shop = shopDAO.getShopDetails();
            int retentionMonths = (months != null && months > 0) ? months : shop.getAutoDeleteMonths();
            
            if (retentionMonths <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Auto-delete is currently disabled (months <= 0)"));
            }

            int deletedCount = billDAO.deleteBillsOlderThanMonths(retentionMonths);
            return ResponseEntity.ok(Map.of(
                "message", "Purge completed successfully",
                "deletedBills", deletedCount,
                "retentionMonths", retentionMonths
            ));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

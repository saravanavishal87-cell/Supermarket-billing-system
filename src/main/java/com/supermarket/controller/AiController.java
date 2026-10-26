package com.supermarket.controller;

import com.supermarket.dto.AiDemandSummary;
import com.supermarket.dto.AiExpirySummary;
import com.supermarket.service.AiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/expiry-report")
    public ResponseEntity<AiExpirySummary> getExpiryReport() {
        return ResponseEntity.ok(aiService.getExpiryReport());
    }

    @GetMapping("/demand-forecast")
    public ResponseEntity<AiDemandSummary> getDemandForecast() {
        return ResponseEntity.ok(aiService.getDemandForecast());
    }

    @GetMapping("/quick-stats")
    public ResponseEntity<?> getQuickStats() {
        try {
            AiExpirySummary expiry = aiService.getExpiryReport();
            AiDemandSummary demand = aiService.getDemandForecast();

            return ResponseEntity.ok(Map.of(
                "expiredCount", expiry.getExpiredCount(),
                "criticalExpiryCount", expiry.getCriticalCount(),
                "wasteRiskValue", expiry.getTotalWasteRiskValue(),
                "salvageValue", expiry.getTotalSalvageableValue(),
                "highDemandCount", demand.getHighDemandCount(),
                "stockoutRiskCount", demand.getStockoutRiskCount(),
                "reorderActions", demand.getTotalReorderActions()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/apply-discount")
    public ResponseEntity<?> applyDiscount(@RequestBody Map<String, Object> body) {
        try {
            int productId = ((Number) body.get("productId")).intValue();
            double discount = ((Number) body.get("discount")).doubleValue();

            boolean success = aiService.applyDiscount(productId, discount);
            if (success) {
                return ResponseEntity.ok(Map.of("success", true, "message", "AI discount of " + discount + "% applied successfully!"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Could not apply discount to product ID " + productId));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody(required = false) com.supermarket.dto.AiChatRequest req) {
        try {
            String prompt = (req != null && req.getQuery() != null) ? req.getQuery() : "";
            Map<String, Object> result = aiService.askCopilot(prompt);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
}

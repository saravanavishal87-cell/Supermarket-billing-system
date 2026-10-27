package com.supermarket.controller;

import com.supermarket.dto.MlClusteringSummary;
import com.supermarket.dto.MlComparisonReport;
import com.supermarket.dto.MlDemandComparisonItem;
import com.supermarket.dto.MlExpiryComparisonItem;
import com.supermarket.service.MlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ml")
public class MlController {

    private final MlService mlService = new MlService();

    @GetMapping("/overview")
    public ResponseEntity<MlComparisonReport> getOverview() {
        return ResponseEntity.ok(mlService.getComparisonReport());
    }

    @GetMapping("/demand")
    public ResponseEntity<List<MlDemandComparisonItem>> getDemandForecast() {
        return ResponseEntity.ok(mlService.getDemandComparison());
    }

    @GetMapping("/expiry")
    public ResponseEntity<List<MlExpiryComparisonItem>> getExpiryClassification() {
        return ResponseEntity.ok(mlService.getExpiryClassification());
    }

    @GetMapping("/clusters")
    public ResponseEntity<MlClusteringSummary> getCustomerClusters(@RequestParam(value = "k", defaultValue = "3") int k) {
        return ResponseEntity.ok(mlService.getCustomerClustering(k));
    }
}

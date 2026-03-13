package org.example.learncore.controller;

import lombok.RequiredArgsConstructor;
import org.example.learncore.dto.ApiResponse;
import org.example.learncore.service.IndexPerformanceService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/demo/index")
@RequiredArgsConstructor
public class IndexPerformanceController {

    private final IndexPerformanceService performanceService;

    @PostMapping("/seed")
    public ApiResponse<String> seed(@RequestParam(defaultValue = "50000") int count) {
        return ApiResponse.success(performanceService.seedData(count));
    }

    @GetMapping("/search-no-index")
    public ApiResponse<Map<String, Object>> searchNoIndex(@RequestParam(defaultValue = "PROD-49999") String sku) {
        Map<String, Object> results = new HashMap<>();
        long time = performanceService.benchmarkNoIndex(sku);
        results.put("status", "Full Table Scan (KHÔNG INDEX)");
        results.put("time_ms", time);
        results.put("sku", sku);
        return ApiResponse.success(results);
    }

    @GetMapping("/search-with-index")
    public ApiResponse<Map<String, Object>> searchWithIndex(@RequestParam(defaultValue = "PROD-49999") String sku) {
        Map<String, Object> results = new HashMap<>();
        long time = performanceService.benchmarkWithIndex(sku);
        results.put("status", "Index Scan (CÓ NON-CLUSTERED INDEX)");
        results.put("time_ms", time);
        results.put("sku", sku);
        return ApiResponse.success(results);
    }

    @GetMapping("/search-id")
    public ApiResponse<Map<String, Object>> searchId(@RequestParam(defaultValue = "1") Long id) {
        Map<String, Object> results = new HashMap<>();
        long time = performanceService.benchmarkClustered(id);
        results.put("status", "Primary Key Access (CLUSTERED INDEX)");
        results.put("time_ms", time);
        results.put("id", id);
        return ApiResponse.success(results);
    }
}

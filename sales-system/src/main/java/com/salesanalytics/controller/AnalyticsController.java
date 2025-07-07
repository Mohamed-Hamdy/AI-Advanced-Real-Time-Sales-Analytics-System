package com.salesanalytics.controller;

import com.salesanalytics.dto.Analytics;
import com.salesanalytics.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AnalyticsController {
    
    private final OrderService orderService;
    
    public AnalyticsController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @GetMapping
    public ResponseEntity<Analytics> getAnalytics() {
        try {
            Analytics analytics = orderService.getAnalytics();
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
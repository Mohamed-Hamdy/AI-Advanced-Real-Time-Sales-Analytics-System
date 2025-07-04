package com.analytics.salessystem.Controller;

import com.analytics.salessystem.Service.OrderService;
import com.analytics.salessystem.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService service;

    @PostMapping("/orders")
    public ResponseEntity<Void> addOrder(@RequestBody Order order) {
        service.addOrder(order);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalRevenue", service.getTotalRevenue());
        response.put("topProducts", service.getTopSellingProducts());
        response.put("recentRevenue", service.getRecentRevenue());
        response.put("recentOrders", service.getRecentOrderCount());
        return ResponseEntity.ok(response);
    }
}

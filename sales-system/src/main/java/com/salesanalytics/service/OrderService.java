package com.salesanalytics.service;

import com.salesanalytics.dto.*;
import com.salesanalytics.entity.Order;
import com.salesanalytics.repository.OrderRepository;
import com.salesanalytics.websocket.SalesWebSocketHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final SalesWebSocketHandler webSocketHandler;
    
    public OrderService(OrderRepository orderRepository, SalesWebSocketHandler webSocketHandler) {
        this.orderRepository = orderRepository;
        this.webSocketHandler = webSocketHandler;
    }
    
    public OrderResponse createOrder(OrderRequest orderRequest) {
        Order order = new Order(
            orderRequest.getProductName(),
            orderRequest.getQuantity(),
            orderRequest.getPrice(),
            orderRequest.getDate()
        );
        
        Order savedOrder = orderRepository.save(order);
        OrderResponse response = convertToResponse(savedOrder);
        
        // Send real-time update via WebSocket
        webSocketHandler.broadcastNewOrder(response);
        webSocketHandler.broadcastAnalyticsUpdate(getAnalytics());
        
        return response;
    }
    
    public Analytics getAnalytics() {
        Double totalRevenue = orderRepository.getTotalRevenue();
        if (totalRevenue == null) totalRevenue = 0.0;
        
        Integer totalOrders = orderRepository.getTotalOrderCount();
        if (totalOrders == null) totalOrders = 0;
        
        List<TopProduct> topProducts = getTopProducts();
        List<OrderResponse> recentOrders = getRecentOrders();
        
        // Calculate revenue change (last minute vs previous minute)
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        LocalDateTime twoMinutesAgo = LocalDateTime.now().minusMinutes(2);
        
        Double revenueLastMinute = orderRepository.getRevenueSince(oneMinuteAgo);
        Double revenuePreviousMinute = orderRepository.getRevenueSince(twoMinutesAgo);
        
        if (revenueLastMinute == null) revenueLastMinute = 0.0;
        if (revenuePreviousMinute == null) revenuePreviousMinute = 0.0;
        
        Double revenueChange = 0.0;
        if (revenuePreviousMinute > 0) {
            revenueChange = ((revenueLastMinute - revenuePreviousMinute) / revenuePreviousMinute) * 100;
        } else if (revenueLastMinute > 0) {
            revenueChange = 100.0;
        }
        
        Integer ordersInLastMinute = orderRepository.countOrdersSince(oneMinuteAgo);
        if (ordersInLastMinute == null) ordersInLastMinute = 0;
        
        return new Analytics(
            totalRevenue,
            totalOrders,
            topProducts,
            recentOrders,
            revenueChange,
            ordersInLastMinute
        );
    }
    
    private List<TopProduct> getTopProducts() {
        List<Object[]> results = orderRepository.getTopProductsByRevenue();
        Double totalRevenue = orderRepository.getTotalRevenue();
        if (totalRevenue == null) totalRevenue = 1.0;

        Double finalTotalRevenue = totalRevenue;
        return results.stream()
            .limit(5)
            .map(result -> {
                String name = (String) result[0];
                Double sales = (Double) result[1];
                Long quantity = (Long) result[2];
                Double percentage = (sales / finalTotalRevenue) * 100;
                
                return new TopProduct(name, sales, quantity.intValue(), percentage);
            })
            .collect(Collectors.toList());
    }
    
    private List<OrderResponse> getRecentOrders() {
        return orderRepository.findRecentOrders()
            .stream()
            .limit(10)
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    private OrderResponse convertToResponse(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getProductName(),
            order.getQuantity(),
            order.getPrice(),
            order.getDate()
        );
    }
}
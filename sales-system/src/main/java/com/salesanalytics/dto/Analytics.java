package com.salesanalytics.dto;

import java.util.List;

public class Analytics {
    private Double totalRevenue;
    private Integer totalOrders;
    private List<TopProduct> topProducts;
    private List<OrderResponse> recentOrders;
    private Double revenueChange;
    private Integer ordersInLastMinute;
    
    // Constructors
    public Analytics() {}
    
    public Analytics(Double totalRevenue, Integer totalOrders, List<TopProduct> topProducts, 
                    List<OrderResponse> recentOrders, Double revenueChange, Integer ordersInLastMinute) {
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.topProducts = topProducts;
        this.recentOrders = recentOrders;
        this.revenueChange = revenueChange;
        this.ordersInLastMinute = ordersInLastMinute;
    }
    
    // Getters and Setters
    public Double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
    
    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }
    
    public List<TopProduct> getTopProducts() { return topProducts; }
    public void setTopProducts(List<TopProduct> topProducts) { this.topProducts = topProducts; }
    
    public List<OrderResponse> getRecentOrders() { return recentOrders; }
    public void setRecentOrders(List<OrderResponse> recentOrders) { this.recentOrders = recentOrders; }
    
    public Double getRevenueChange() { return revenueChange; }
    public void setRevenueChange(Double revenueChange) { this.revenueChange = revenueChange; }
    
    public Integer getOrdersInLastMinute() { return ordersInLastMinute; }
    public void setOrdersInLastMinute(Integer ordersInLastMinute) { this.ordersInLastMinute = ordersInLastMinute; }
}
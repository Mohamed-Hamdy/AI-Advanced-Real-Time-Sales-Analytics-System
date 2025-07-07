package com.salesanalytics.dto;

import java.time.LocalDateTime;

public class OrderResponse {
    private Long id;
    private String productName;
    private Integer quantity;
    private Double price;
    private LocalDateTime date;
    private Double total;
    
    // Constructors
    public OrderResponse() {}
    
    public OrderResponse(Long id, String productName, Integer quantity, Double price, LocalDateTime date) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.date = date;
        this.total = quantity * price;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
}
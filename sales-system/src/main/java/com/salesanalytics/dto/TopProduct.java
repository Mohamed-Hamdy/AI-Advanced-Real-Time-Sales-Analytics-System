package com.salesanalytics.dto;

public class TopProduct {
    private String name;
    private Double totalSales;
    private Integer quantity;
    private Double percentage;
    
    // Constructors
    public TopProduct() {}
    
    public TopProduct(String name, Double totalSales, Integer quantity, Double percentage) {
        this.name = name;
        this.totalSales = totalSales;
        this.quantity = quantity;
        this.percentage = percentage;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Double getTotalSales() { return totalSales; }
    public void setTotalSales(Double totalSales) { this.totalSales = totalSales; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }
}
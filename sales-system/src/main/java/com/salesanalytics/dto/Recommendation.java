package com.salesanalytics.dto;

public class Recommendation {
    private String id;
    private String title;
    private String description;
    private String type;
    private String priority;
    private String impact;
    
    // Constructors
    public Recommendation() {}
    
    public Recommendation(String id, String title, String description, String type, String priority, String impact) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.priority = priority;
        this.impact = impact;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public String getImpact() { return impact; }
    public void setImpact(String impact) { this.impact = impact; }
}
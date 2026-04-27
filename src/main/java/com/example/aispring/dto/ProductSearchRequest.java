package com.example.aispring.dto;

/**
 * Request DTO for product search
 */
public class ProductSearchRequest {
    private String query;
    private String category;
    private Double maxPrice;
    private Boolean useAIEnhancement;
    
    public ProductSearchRequest() {}
    
    public ProductSearchRequest(String query, String category, Double maxPrice, Boolean useAIEnhancement) {
        this.query = query;
        this.category = category;
        this.maxPrice = maxPrice;
        this.useAIEnhancement = useAIEnhancement;
    }
    
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public Double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }
    
    public Boolean getUseAIEnhancement() { return useAIEnhancement; }
    public void setUseAIEnhancement(Boolean useAIEnhancement) { this.useAIEnhancement = useAIEnhancement; }
}

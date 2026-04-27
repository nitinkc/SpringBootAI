package com.example.aispring.dto;

/**
 * Request DTO for recommendation
 */
public class RecommendationRequest {
    private Long userId;
    private Integer limit;
    private String category;
    
    public RecommendationRequest() {}
    
    public RecommendationRequest(Long userId, Integer limit, String category) {
        this.userId = userId;
        this.limit = limit;
        this.category = category;
    }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Integer getLimit() { return limit; }
    public void setLimit(Integer limit) { this.limit = limit; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}

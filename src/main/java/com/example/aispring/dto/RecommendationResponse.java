package com.example.aispring.dto;

import java.util.List;

/**
 * Response DTO for personalized recommendations
 */
public class RecommendationResponse {
    private Long userId;
    private List<ProductResult> recommendations;
    private String personalizationReasoning;
    private String aiModel;
    
    public RecommendationResponse() {}
    
    public RecommendationResponse(Long userId, List<ProductResult> recommendations, String personalizationReasoning, String aiModel) {
        this.userId = userId;
        this.recommendations = recommendations;
        this.personalizationReasoning = personalizationReasoning;
        this.aiModel = aiModel;
    }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public List<ProductResult> getRecommendations() { return recommendations; }
    public void setRecommendations(List<ProductResult> recommendations) { this.recommendations = recommendations; }
    
    public String getPersonalizationReasoning() { return personalizationReasoning; }
    public void setPersonalizationReasoning(String personalizationReasoning) { this.personalizationReasoning = personalizationReasoning; }
    
    public String getAiModel() { return aiModel; }
    public void setAiModel(String aiModel) { this.aiModel = aiModel; }
    
    public static class ProductResult {
        private Long id;
        private String name;
        private Double price;
        private String reason;
        private Double confidenceScore;
        
        public ProductResult() {}
        
        public ProductResult(Long id, String name, Double price, String reason, Double confidenceScore) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.reason = reason;
            this.confidenceScore = confidenceScore;
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        
        public Double getConfidenceScore() { return confidenceScore; }
        public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }
    }
}

package com.example.aispring.dto;

import java.util.List;

/**
 * Response DTO for product search with AI enhancement
 */
public class ProductSearchResponse {
    private List<ProductResult> results;
    private String aiSuggestion;
    private boolean enhancedWithAI;
    private String reasoningExplanation;
    
    public ProductSearchResponse() {}
    
    public List<ProductResult> getResults() { return results; }
    public void setResults(List<ProductResult> results) { this.results = results; }
    
    public String getAiSuggestion() { return aiSuggestion; }
    public void setAiSuggestion(String aiSuggestion) { this.aiSuggestion = aiSuggestion; }
    
    public boolean isEnhancedWithAI() { return enhancedWithAI; }
    public void setEnhancedWithAI(boolean enhancedWithAI) { this.enhancedWithAI = enhancedWithAI; }
    
    public String getReasoningExplanation() { return reasoningExplanation; }
    public void setReasoningExplanation(String reasoningExplanation) { this.reasoningExplanation = reasoningExplanation; }
    
    public static class ProductResult {
        private Long id;
        private String name;
        private String description;
        private Double price;
        private String category;
        private Integer stockQuantity;
        private String relevanceScore;
        
        public ProductResult() {}
        
        public ProductResult(Long id, String name, String description, Double price, String category, Integer stockQuantity, String relevanceScore) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.category = category;
            this.stockQuantity = stockQuantity;
            this.relevanceScore = relevanceScore;
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public Integer getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
        
        public String getRelevanceScore() { return relevanceScore; }
        public void setRelevanceScore(String relevanceScore) { this.relevanceScore = relevanceScore; }
    }
}

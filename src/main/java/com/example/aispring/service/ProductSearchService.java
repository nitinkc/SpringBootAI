package com.example.aispring.service;

import com.example.aispring.client.AiClient;
import com.example.aispring.dto.ProductSearchRequest;
import com.example.aispring.dto.ProductSearchResponse;
import com.example.aispring.model.Product;
import com.example.aispring.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Product Search Service with AI Enhancement
 * 
 * Traditional Flow: Search DB → Return results
 * AI-Enhanced Flow: Search DB → Build context → Call LLM → Enhance results → Return smarter response
 * 
 * Shows how AI is integrated inside the service layer without redesigning the system.
 */
@Service
public class ProductSearchService {
    
    private final ProductRepository productRepository;
    private final AiClient aiClient;
    
    public ProductSearchService(ProductRepository productRepository, AiClient aiClient) {
        this.productRepository = productRepository;
        this.aiClient = aiClient;
    }
    
    /**
     * Search products with optional AI enhancement
     * 
     * The service layer:
     * 1. Fetches data from database
     * 2. Builds context about the search
     * 3. Optionally calls the LLM
     * 4. Returns enhanced response
     */
    public ProductSearchResponse searchProducts(ProductSearchRequest request) {
        long startTime = System.currentTimeMillis();
        
        // Step 1: Fetch data from database
        List<Product> results = searchDatabase(request);
        
        ProductSearchResponse response = new ProductSearchResponse();
        response.setEnhancedWithAI(false);
        
        // Step 2: If AI enhancement is requested
        if (request.getUseAIEnhancement() != null && request.getUseAIEnhancement()) {
            
            // Step 3: Build context about the search and results
            String context = buildSearchContext(request, results);
            
            // Step 4: Call the LLM to get intelligent suggestions
            String aiResponse = callAiForEnhancement(context);
            
            response.setAiSuggestion(aiResponse);
            response.setEnhancedWithAI(true);
            response.setReasoningExplanation("Results enhanced with AI analysis of your query and available products");
        }
        
        // Step 5: Transform results to response DTOs
        response.setResults(transformToDto(results));
        
        return response;
    }
    
    /**
     * Step 1: Traditional database search
     */
    private List<Product> searchDatabase(ProductSearchRequest request) {
        List<Product> results;
        
        if (request.getQuery() != null && !request.getQuery().isEmpty()) {
            results = productRepository.searchProducts(request.getQuery());
        } else if (request.getCategory() != null) {
            results = productRepository.findByCategory(request.getCategory());
        } else {
            results = productRepository.findAll();
        }
        
        // Filter by price if specified
        if (request.getMaxPrice() != null) {
            results = results.stream()
                .filter(p -> p.getPrice() <= request.getMaxPrice())
                .collect(Collectors.toList());
        }
        
        return results;
    }
    
    /**
     * Step 2: Build context for the LLM
     * This is the "data preparation" before calling the AI
     */
    private String buildSearchContext(ProductSearchRequest request, List<Product> results) {
        StringBuilder context = new StringBuilder();
        
        context.append("User Search Context:\n");
        context.append("- Query: ").append(request.getQuery()).append("\n");
        if (request.getCategory() != null) {
            context.append("- Category: ").append(request.getCategory()).append("\n");
        }
        if (request.getMaxPrice() != null) {
            context.append("- Max Price: $").append(request.getMaxPrice()).append("\n");
        }
        
        context.append("\nAvailable Products (").append(results.size()).append(" found):\n");
        
        results.forEach(product -> 
            context.append(String.format(
                "- %s ($%.2f): %s [Category: %s]\n",
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                product.getCategory()
            ))
        );
        
        return context.toString();
    }
    
    /**
     * Step 3: Call the LLM for AI enhancement
     * This is where the LLM integration happens in the service layer
     */
    private String callAiForEnhancement(String context) {
        String systemPrompt = "You are a helpful product recommendation assistant. " +
                             "Analyze the user's query and the available products. " +
                             "Provide personalized recommendations and insights.";
        
        String userPrompt = context + 
                           "\nBased on the search query and available products, " +
                           "what would you recommend and why?";
        
        if (aiClient.isAvailable()) {
            return aiClient.generateResponseWithContext(systemPrompt, userPrompt);
        } else {
            return "AI enhancement unavailable. Using traditional search results only.";
        }
    }
    
    /**
     * Step 5: Transform to response DTO
     */
    private List<ProductSearchResponse.ProductResult> transformToDto(List<Product> products) {
        return products.stream()
            .map(product -> new ProductSearchResponse.ProductResult(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getStockQuantity(),
                "HIGH" // In real scenario, this would be calculated by AI
            ))
            .collect(Collectors.toList());
    }
}

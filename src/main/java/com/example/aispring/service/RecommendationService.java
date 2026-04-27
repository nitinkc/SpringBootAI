package com.example.aispring.service;

import com.example.aispring.client.AiClient;
import com.example.aispring.dto.RecommendationRequest;
import com.example.aispring.dto.RecommendationResponse;
import com.example.aispring.model.Product;
import com.example.aispring.model.User;
import com.example.aispring.repository.ProductRepository;
import com.example.aispring.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Recommendation Service with AI-Powered Personalization
 * 
 * Traditional Flow: Get user profile → Show generic products → Return list
 * AI-Enhanced Flow: Get user profile → Get products → Call LLM with context → Personalized recommendations
 * 
 * Demonstrates how AI adds intelligence to service layer without system redesign.
 */
@Service
public class RecommendationService {
    
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final AiClient aiClient;
    
    public RecommendationService(UserRepository userRepository, ProductRepository productRepository, AiClient aiClient) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.aiClient = aiClient;
    }
    
    /**
     * Generate personalized recommendations
     * 
     * The service layer:
     * 1. Fetches user profile and preferences
     * 2. Fetches available products
     * 3. Builds context about user and products
     * 4. Calls the LLM to select best recommendations
     * 5. Returns personalized recommendations
     */
    public RecommendationResponse getPersonalizedRecommendations(RecommendationRequest request) {
        
        // Step 1: Fetch user profile
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Step 2: Fetch available products
        List<Product> allProducts = productRepository.findAll();
        
        // Filter by category if specified
        if (request.getCategory() != null) {
            allProducts = allProducts.stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(request.getCategory()))
                .collect(Collectors.toList());
        }
        
        // Step 3: Build context for the LLM
        String context = buildRecommendationContext(user, allProducts);
        
        // Step 4: Call the LLM to generate personalized recommendations
        String aiAnalysis = generateAiRecommendations(context) ;
        
        // Step 5: Transform results and return
        return buildRecommendationResponse(user, allProducts, aiAnalysis, request.getLimit());
    }
    
    /**
     * Step 3: Build context for the LLM
     * This includes user profile and available products
     */
    private String buildRecommendationContext(User user, List<Product> products) {
        StringBuilder context = new StringBuilder();
        
        context.append("User Profile:\n");
        context.append("- Name: ").append(user.getUsername()).append("\n");
        context.append("- Email: ").append(user.getEmail()).append("\n");
        context.append("- Purchase History: ").append(user.getPurchaseHistory()).append("\n");
        context.append("- Preferences: ").append(user.getPreferences()).append("\n");
        
        context.append("\nAvailable Products:\n");
        products.forEach(product ->
            context.append(String.format(
                "%d. %s - $%.2f (%s): %s\n",
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategory(),
                product.getDescription()
            ))
        );
        
        return context.toString();
    }
    
    /**
     * Step 4: Call the LLM to generate personalized recommendations
     * This is where AI powers the service layer
     */
    private String generateAiRecommendations(String context) {
        String systemPrompt = "You are a personal shopping assistant with expertise in understanding " +
                             "customer preferences and recommending products. " +
                             "Analyze the user's profile and purchase history to suggest products " +
                             "they would genuinely enjoy. Provide clear reasoning for each recommendation.";
        
        String userPrompt = context +
                           "\nBased on this user's profile and the available products, " +
                           "recommend the top 5 products they would most likely purchase. " +
                           "For each recommendation, explain why this product matches their interests.";
        
        if (aiClient.isAvailable()) {
            return aiClient.generateResponseWithContext(systemPrompt, userPrompt);
        } else {
            return "Recommendations unavailable. Showing popular products instead.";
        }
    }
    
    /**
     * Step 5: Build response with recommendations
     */
    private RecommendationResponse buildRecommendationResponse(
            User user, 
            List<Product> products, 
            String aiAnalysis,
            Integer limit) {
        
        int recommendationLimit = limit != null ? limit : 5;
        
        // In a real scenario, we would parse the AI response and rank products
        // For demo, we'll return top products with mock confidence scores
        List<RecommendationResponse.ProductResult> recommendations = products.stream()
            .limit(recommendationLimit)
            .map(product -> new RecommendationResponse.ProductResult(
                product.getId(),
                product.getName(),
                product.getPrice(),
                generateReasonForProduct(product, aiAnalysis),
                Math.random() * 0.4 + 0.6  // Mock confidence between 0.6-1.0
            ))
            .collect(Collectors.toList());
        
        return new RecommendationResponse(
            user.getId(),
            recommendations,
            aiAnalysis,
            aiClient.getModelName()
        );
    }
    
    /**
     * Generate reason for why a product was recommended
     */
    private String generateReasonForProduct(Product product, String aiAnalysis) {
        if (aiAnalysis.toLowerCase().contains("premium")) {
            return "Premium quality matches your preference for durability";
        }
        if (aiAnalysis.toLowerCase().contains("affordable")) {
            return "Great value option suitable for your budget";
        }
        return "Based on your profile and purchase history";
    }
}

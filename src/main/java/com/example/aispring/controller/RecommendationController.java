package com.example.aispring.controller;

import com.example.aispring.dto.RecommendationRequest;
import com.example.aispring.dto.RecommendationResponse;
import com.example.aispring.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Personalized Recommendations
 * 
 * Demonstrates AI-powered personalization:
 * Client → Controller → Service (AI analysis) → DB/APIs → Response
 */
@RestController
@RequestMapping("/recommendations")
public class RecommendationController {
    
    private final RecommendationService recommendationService;
    
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }
    
    /**
     * Get personalized product recommendations for a user
     * 
     * Examples:
     * GET /api/recommendations?userId=1&limit=5
     * GET /api/recommendations?userId=1&category=electronics&limit=3
     */
    @GetMapping
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String category) {
        
        RecommendationRequest request = new RecommendationRequest(userId, limit, category);
        RecommendationResponse response = recommendationService.getPersonalizedRecommendations(request);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get recommendations with POST request
     * 
     * Example:
     * POST /api/recommendations
     * {
     *   "userId": 1,
     *   "limit": 5,
     *   "category": "electronics"
     * }
     */
    @PostMapping
    public ResponseEntity<RecommendationResponse> getRecommendationsPost(
            @RequestBody RecommendationRequest request) {
        RecommendationResponse response = recommendationService.getPersonalizedRecommendations(request);
        
        return ResponseEntity.ok(response);
    }
}

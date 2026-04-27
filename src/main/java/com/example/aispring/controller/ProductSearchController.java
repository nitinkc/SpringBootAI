package com.example.aispring.controller;

import com.example.aispring.dto.ProductSearchRequest;
import com.example.aispring.dto.ProductSearchResponse;
import com.example.aispring.service.ProductSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Product Search
 * 
 * Demonstrates the traditional REST pattern:
 * Client → Controller → Service (with AI) → DB/APIs → Response
 */
@RestController
@RequestMapping("/products/search")
public class ProductSearchController {
    
    private final ProductSearchService productSearchService;
    
    public ProductSearchController(ProductSearchService productSearchService) {
        this.productSearchService = productSearchService;
    }
    
    /**
     * Search products with optional AI enhancement
     * 
     * Examples:
     * GET /api/products/search?query=laptop&useAIEnhancement=true
     * GET /api/products/search?category=electronics&maxPrice=1000&useAIEnhancement=true
     */
    @GetMapping
    public ResponseEntity<ProductSearchResponse> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "false") Boolean useAIEnhancement) {
        
        ProductSearchRequest request = new ProductSearchRequest(query, category, maxPrice, useAIEnhancement);
        ProductSearchResponse response = productSearchService.searchProducts(request);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Search with POST request and complex body
     * 
     * Example:
     * POST /api/products/search
     * {
     *   "query": "high performance laptop",
     *   "category": "electronics",
     *   "maxPrice": 2000,
     *   "useAIEnhancement": true
     * }
     */
    @PostMapping
    public ResponseEntity<ProductSearchResponse> searchWithBody(
            @RequestBody ProductSearchRequest request) {
        ProductSearchResponse response = productSearchService.searchProducts(request);
        
        return ResponseEntity.ok(response);
    }
}

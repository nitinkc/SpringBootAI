# Service Layer AI Pattern

## The Pattern: Service Layer Orchestration

The AI-enabled service layer acts as an **orchestrator** that:

1. Handles business logic
2. Fetches required data
3. Builds context for LLM
4. Calls AI when appropriate
5. Validates and processes output
6. Returns enhanced results

## Anatomy of an AI Service

### Step-by-Step Flow

```java
@Service
public class ProductSearchService {
    
    private final ProductRepository productRepository;
    private final AiClient aiClient;
    
    public ProductSearchResponse searchProducts(ProductSearchRequest request) {
        // Step 1: Fetch data from database
        List<Product> results = productRepository.searchProducts(request.getQuery());
        
        // Step 2: Build context (business logic + data)
        String context = buildSearchContext(request, results);
        
        // Step 3: Call LLM with context
        String aiResponse = aiClient.generateResponseWithContext(systemPrompt, context);
        
        // Step 4: Validate and process LLM output
        String enhancedSuggestion = validateAndClean(aiResponse);
        
        // Step 5: Build response to client
        return buildResponse(results, enhancedSuggestion);
    }
}
```

### Responsibilities

| Responsibility | Example | Why Service Layer |
|---|---|---|
| Fetch Data | Query ProductRepository | Service has repository access |
| Build Context | Add user preferences, product details | Service understands domain |
| Call AI | AiClient.generateResponse() | Service controls when to call |
| Validate Output | Ensure response matches schema | Service knows business rules |
| Enhance Results | Add scores, filter, rank | Service can enhance original data |

## Key Concept: Separation of Concerns

### ❌ Anti-Pattern: AI in Controller

```java
@RestController
@RequestMapping("/products/search")
public class ProductSearchController {
    
    private final AiClient aiClient;
    
    @GetMapping
    public ResponseEntity<Response> search(@RequestParam String query) {
        // ❌ Bad: Controller is handling business logic
        List<Product> results = repository.search(query);
        String context = buildComplexContext(results);
        String aiResponse = aiClient.generateResponse(context);
        // Controller now knows about:
        // - Prompt engineering
        // - LLM error handling
        // - Response parsing
        // This violates SRP!
    }
}
```

**Problems:**
- Controller has too many responsibilities
- Hard to test
- Couples HTTP handling with AI logic
- Can't reuse logic if called from other endpoints

### ✅ Pattern: AI in Service

```java
@Service
public class ProductSearchService {
    
    private final ProductRepository productRepository;
    private final AiClient aiClient;
    
    public ProductSearchResponse searchProducts(ProductSearchRequest request) {
        // All AI orchestration here
        return enhanceWithAI(request);
    }
    
    private ProductSearchResponse enhanceWithAI(ProductSearchRequest request) {
        // Complex context building
        // Error handling
        // Prompt engineering
        // Response parsing
        // All encapsulated in service
    }
}

@RestController
public class ProductSearchController {
    
    private final ProductSearchService service;
    
    @GetMapping("/products/search")
    public ResponseEntity<ProductSearchResponse> search(ProductSearchRequest req) {
        // ✅ Good: Controller delegates to service
        return ResponseEntity.ok(service.searchProducts(req));
    }
}
```

**Benefits:**
- Single Responsibility Principle
- Easy to test (mock service or AI client)
- Reusable from other controllers/APIs
- Clean separation

## Context Building Strategy

The quality of AI output depends on the quality of context provided.

### Poor Context: Vague
```java
String prompt = "User: " + request.getQuery();
// LLM doesn't know:
// - What products are available?
// - What is the user's history?
// - What should we recommend?
```

### Good Context: Rich and Structured
```java
String context = buildSearchContext(request, results);
// Returns something like:
/*
USER SEARCH QUERY: "laptop with good performance"
SEARCH FILTERS:
- Category: Electronics
- Max Price: $1500

AVAILABLE PRODUCTS (6 found):
1. Premium Laptop Pro - $1899
   - Processor: Intel i9
   - RAM: 32GB
   - GPU: RTX 4080
   - Storage: 1TB SSD
   - Category: Electronics

2. Budget Laptop Basic - $599
   - Processor: Intel i5
   - RAM: 8GB
   - GPU: Integrated
   - Storage: 256GB SSD
   - Category: Electronics

[... more products ...]

TASK: Analyze user query and available products. 
Recommend the BEST 3 products for this user and explain why.
*/
```

### Building Context in Code

```java
private String buildSearchContext(ProductSearchRequest request, List<Product> results) {
    StringBuilder context = new StringBuilder();
    
    // User's intent
    context.append("USER SEARCH:\n");
    context.append("- Query: ").append(request.getQuery()).append("\n");
    context.append("- Category: ").append(request.getCategory()).append("\n");
    context.append("- Max Budget: $").append(request.getMaxPrice()).append("\n");
    context.append("\n");
    
    // Available products with details
    context.append("AVAILABLE PRODUCTS (").append(results.size()).append(" found):\n");
    results.forEach((product, index) -> {
        context.append(index + 1).append(". ").append(product.getName()).append("\n");
        context.append("   Price: $").append(product.getPrice()).append("\n");
        context.append("   Description: ").append(product.getDescription()).append("\n");
        context.append("   Stock: ").append(product.getStockQuantity()).append("\n");
        context.append("\n");
    });
    
    return context.toString();
}
```

## Error Handling Strategy

### Graceful Degradation

```java
try {
    // Primary: Try with AI enhancement
    if (aiClient.isAvailable()) {
        String aiResponse = aiClient.generateResponse(context);
        return buildEnhancedResponse(results, aiResponse);
    }
} catch (TimeoutException e) {
    logger.warn("AI request timed out, using fallback");
    // Monitor: Log for observability
} catch (RateLimitException e) {
    logger.warn("AI rate limit exceeded, using fallback");
    // Monitor: Track rate limit issues
} catch (Exception e) {
    logger.error("Unexpected AI error", e);
    // Monitor: Alert on unexpected errors
}

// Fallback: Return traditional results
return buildTraditionalResponse(results);
```

### Timeout Management

```java
try {
    // Set a timeout for AI calls
    String response = aiClient.generateResponseWithTimeout(
        prompt, 
        Duration.ofSeconds(3)  // Don't wait longer than 3s
    );
} catch (TimeoutException e) {
    // Don't let LLM delay user experience
    return fallbackResponse;
}
```

## Caching Patterns

### Full Response Caching
```java
@Service
@CacheConfig(cacheNames = "productSearchCache")
public class ProductSearchService {
    
    @Cacheable(key = "#request.query")
    public ProductSearchResponse searchProducts(ProductSearchRequest request) {
        // First call hits DB and LLM
        // Second identical call uses cache
        return computeResponse(request);
    }
}
```

### Partial Caching
```java
public ProductSearchResponse searchProducts(ProductSearchRequest request) {
    List<Product> results = repository.search(request.getQuery()); // Always fresh
    
    // Cache the expensive AI part
    String aiSuggestion = cache.getOrCompute(
        cacheKey(request),
        () -> aiClient.generateResponse(buildContext(results))
    );
    
    return buildResponse(results, aiSuggestion);
}
```

## Decision Points in Service

### When to Call AI?

```java
public ProductSearchResponse searchProducts(ProductSearchRequest request) {
    List<Product> results = repository.search(request.getQuery());
    
    // Decision 1: User asked for AI enhancement
    if (request.getUseAIEnhancement() == null || !request.getUseAIEnhancement()) {
        return buildTraditionalResponse(results);
    }
    
    // Decision 2: Enough results to analyze
    if (results.isEmpty()) {
        return buildEmptyResponse();
    }
    
    // Decision 3: AI client is available
    if (!aiClient.isAvailable()) {
        return buildTraditionalResponse(results);
    }
    
    // Decision 4: Within rate limits
    if (rateLimiter.isExceeded()) {
        return buildTraditionalResponse(results);
    }
    
    // All checks pass: Call AI
    return enhanceWithAI(results, request);
}
```

## Testing Strategies

### Unit Test with Mock AI

```java
@SpringBootTest
public class ProductSearchServiceTest {
    
    @MockBean
    private AiClient aiClient;
    
    @Autowired
    private ProductSearchService service;
    
    @Test
    void testSearchWithAIEnhancement() {
        // Arrange
        when(aiClient.generateResponse(anyString()))
            .thenReturn("MockAI response: Recommend laptop X");
        
        ProductSearchRequest request = new ProductSearchRequest(
            "laptop", null, null, true
        );
        
        // Act
        ProductSearchResponse response = service.searchProducts(request);
        
        // Assert
        assertTrue(response.isEnhancedWithAI());
        assertNotNull(response.getAiSuggestion());
    }
    
    @Test
    void testFallbackWhenAIUnavailable() {
        // Arrange
        when(aiClient.isAvailable()).thenReturn(false);
        
        // Act
        ProductSearchResponse response = service.searchProducts(request);
        
        // Assert
        assertFalse(response.isEnhancedWithAI());
    }
}
```

## Real-World Considerations

### Thread Safety
If calling multiple LLM endpoints, consider async:

```java
public async CompletableFuture<ProductSearchResponse> searchAsync(
    ProductSearchRequest request) {
    
    // Fetch data in parallel
    CompletableFuture<List<Product>> productsFuture = 
        CompletableFuture.supplyAsync(() -> 
            repository.search(request.getQuery())
        );
    
    CompletableFuture<List<Product>> relatedFuture = 
        CompletableFuture.supplyAsync(() -> 
            repository.findRelated(request.getCategory())
        );
    
    // Combine results
    return CompletableFuture.allOf(productsFuture, relatedFuture)
        .thenApplyAsync(v -> enhanceWithAI(
            productsFuture.join(),
            relatedFuture.join()
        ));
}
```

### Resource Cleanup
```java
try (var aiContext = aiClient.createContext()) {
    String response = aiContext.generate(prompt);
    return process(response);
} catch (Exception e) {
    // Resources automatically cleaned up
    throw new AIException(e);
}
```

---

Next: [Abstraction & Dependency Injection](abstraction-di.md) →

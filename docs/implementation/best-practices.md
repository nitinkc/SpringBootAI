# Production-Ready Best Practices

## 1. Always Provide Fallbacks

```java
@Service
public class ProductSearchService {
    
    public ProductSearchResponse search(ProductSearchRequest req) {
        try {
            // Primary: Database search
            List<Product> dbResults = db.search(req);
            
            // Secondary: AI ranking (optional enhancement)
            if (shouldEnhanceWithAi(req)) {
                try {
                    List<Product> aiRanked = enhanceWithAi(dbResults);
                    return success(aiRanked, true);  // AI worked
                } catch (Exception e) {
                    log.warn("AI ranking failed, using DB results", e);
                    return success(dbResults, false);  // Fall back gracefully
                }
            }
            
            return success(dbResults, false);
            
        } catch (Exception e) {
            log.error("Search failed", e);
            throw new SearchException("Search unavailable", e);
        }
    }
}
```

## 2. Use Circuit Breaker Pattern

```java
@Service
public class AiCircuitBreaker {
    
    private final CircuitBreaker breaker;
    private final AiClient aiClient;
    
    public AiCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50)          // Fail if >50% errors
            .slowCallRateThreshold(50)         // Fail if >50% slow
            .slowCallDurationThreshold(5000)   // >5s = slow
            .minimumNumberOfCalls(10)          // Need 10 calls to measure
            .waitDurationInOpenState(60000)    // Wait 60s before retry
            .build();
        
        this.breaker = CircuitBreaker.of("aiClient", config);
    }
    
    public String generateResponse(String prompt) {
        return breaker.executeSupplier(() -> 
            aiClient.generateResponse(prompt)
        );
    }
}
```

## 3. Timeout Strategy

```java
@Service
public class TimeoutAiClient implements AiClient {
    
    private final RestTemplate rest;
    
    public String generateResponse(String prompt) {
        // 1. Short timeout for user-facing calls (<100ms)
        // 2. Longer timeout for background tasks (5s)
        
        Duration timeout = isUserFacing() ? 
            Duration.ofMillis(100) : 
            Duration.ofSeconds(5);
        
        try {
            return rest.postForObject("/api/generate",
                new Request(prompt),
                String.class
            );
        } catch (ResourceAccessException e) {
            throw new AiTimeoutException("AI timeout", e);
        }
    }
}
```

## 4. Request Validation

```java
@Service
public class ProductSearchService {
    
    public ProductSearchResponse search(ProductSearchRequest req) {
        // Validate before calling AI
        validateRequest(req);
        
        if (req.getQuery().isEmpty()) {
            throw new InvalidRequestException("Query required");
        }
        
        if (req.getQuery().length() > 500) {
            throw new InvalidRequestException("Query too long");
        }
        
        // Validate response from AI
        String aiResponse = aiClient.generateResponse(buildPrompt(req));
        validateAiResponse(aiResponse);
        
        return parseResponse(aiResponse);
    }
    
    private void validateAiResponse(String response) {
        if (response == null || response.isEmpty()) {
            throw new InvalidResponseException("Empty response");
        }
        
        if (!response.contains("[") || !response.contains("]")) {
            throw new InvalidResponseException("Invalid JSON");
        }
    }
}
```

## 5. Rate Limiting

```java
@Configuration
public class RateLimitConfig {
    
    @Bean
    public RateLimiter aiCallLimiter() {
        return RateLimiter.create(100);  // 100 requests/second max
    }
}

@Service
public class RateLimitedAiClient implements AiClient {
    
    private final RateLimiter limiter;
    private final AiClient delegate;
    
    @Override
    public String generateResponse(String prompt) {
        if (!limiter.tryAcquire(1, 5, TimeUnit.SECONDS)) {
            throw new RateLimitExceededException("Too many requests");
        }
        return delegate.generateResponse(prompt);
    }
}
```

## 6. Logging & Monitoring

```java
@Service
public class MonitoredAiClient implements AiClient {
    
    private final MeterRegistry metrics;
    private final AiClient delegate;
    
    @Override
    public String generateResponse(String prompt) {
        long start = System.currentTimeMillis();
        
        try {
            String result = delegate.generateResponse(prompt);
            
            long duration = System.currentTimeMillis() - start;
            metrics.timer("ai.latency").record(duration, TimeUnit.MILLISECONDS);
            metrics.counter("ai.success").increment();
            
            return result;
            
        } catch (Exception e) {
            metrics.counter("ai.error").increment();
            throw e;
        }
    }
}
```

---

[Next: Performance Optimization](performance.md)

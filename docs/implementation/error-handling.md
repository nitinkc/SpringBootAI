# Comprehensive Error Handling

## Error Types & Strategies

### 1. Timeout Errors

**Cause:** AI takes >threshold time

```java
@Service
public class TimeoutHandler {
    
    public String handleTimeout(String prompt) {
        try {
            return aiClient.generateResponse(prompt)
                .orTimeout(200, TimeUnit.MILLISECONDS)  // User-facing
                .get();
        } catch (TimeoutException e) {
            log.warn("AI timeout, using fallback", e);
            metrics.counter("ai.timeout").increment();
            
            // Fallback 1: Cached response
            String cached = cache.get(hashPrompt(prompt));
            if (cached != null) return cached;
            
            // Fallback 2: Traditional ranking
            return traditionalResponse(prompt);
        }
    }
}
```

### 2. Network Errors

**Cause:** Network unavailable, DNS failures

```java
@Service
public class NetworkErrorHandler {
    
    private static final int MAX_RETRIES = 3;
    
    public String handleNetworkError(String prompt) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return aiClient.generateResponse(prompt);
            } catch (IOException e) {
                if (attempt < MAX_RETRIES) {
                    // Exponential backoff: 100ms, 200ms, 400ms
                    long backoff = (long) (100 * Math.pow(2, attempt - 1));
                    Thread.sleep(backoff);
                } else {
                    log.error("All retries failed", e);
                    metrics.counter("ai.network_error").increment();
                    throw new ServiceUnavailableException("AI service down", e);
                }
            }
        }
    }
}
```

### 3. Invalid Responses

**Cause:** AI returns malformed/unparseable response

```java
@Service
public class ResponseValidator {
    
    public List<Product> parseAiResponse(String response) {
        try {
            // Validate JSON structure
            JsonNode root = objectMapper.readTree(response);
            
            if (!root.isArray()) {
                throw new InvalidResponseException(
                    "Response must be array, got: " + 
                    root.getNodeType()
                );
            }
            
            // Validate each item
            List<Product> products = new ArrayList<>();
            for (JsonNode item : root) {
                if (!item.has("productId") || !item.has("rank")) {
                    throw new InvalidResponseException(
                        "Missing required fields"
                    );
                }
                products.add(parseProduct(item));
            }
            
            return products;
            
        } catch (IOException e) {
            log.error("Failed to parse AI response", e);
            metrics.counter("ai.parse_error").increment();
            throw new InvalidResponseException("Unparseable response", e);
        }
    }
}
```

### 4. Rate Limiting / Cost Control

**Cause:** Too many API calls, budget exceeded

```java
@Service
public class CostController {
    
    private final AtomicDouble dailySpend = new AtomicDouble(0);
    private final double dailyBudget = 100;
    
    public String handleBudgetExceeded(String prompt) {
        double requestCost = estimateCost(prompt);
        
        if (dailySpend.get() + requestCost > dailyBudget) {
            log.warn("Daily budget exceeded, using fallback");
            metrics.counter("budget.exceeded").increment();
            
            // Fallback: Traditional response without AI
            return traditionalResponse(prompt);
        }
        
        String result = aiClient.generateResponse(prompt);
        dailySpend.addAndGet(requestCost);
        
        return result;
    }
}
```

### 5. Provider Failure (Multi-Provider Fallback)

**Cause:** Selected provider unavailable

```java
@Service
public class MultiProviderAiClient implements AiClient {
    
    private final List<AiClient> providers = Arrays.asList(
        new OpenAiClient(),
        new AnthropicClient(),
        new OllamaClient()
    );
    
    @Override
    public String generateResponse(String prompt) {
        Exception lastError = null;
        
        for (AiClient provider : providers) {
            try {
                return provider.generateResponse(prompt);
            } catch (Exception e) {
                lastError = e;
                log.warn("Provider failed, trying next", e);
                metrics.counter("provider.failover")
                    .tag("provider", provider.getModelName())
                    .increment();
            }
        }
        
        // All providers failed
        log.error("All AI providers unavailable", lastError);
        throw new AllProvidersFailedException("No AI available", lastError);
    }
}
```

## Error Recovery Strategies

| Error Type | Strategy | Risk | Impact |
|---|---|---|---|
| Timeout | Fallback | Low | Slight quality decrease |
| Network | Retry + exponential backoff | Low | Latency increase |
| Invalid response | Use cached response | Medium | Stale data |
| Rate limiting | Use traditional method | Medium | Loss of enhancement |
| All providers down | Return default results | High | Feature disabled |

## Monitoring & Alerting

```yaml
alerts:
  - name: HighErrorRate
    condition: error_rate > 0.05
    duration: 5m
    action: page_oncall
    
  - name: AllProvidersFailed
    condition: ai_available == false
    duration: 30s
    action: page_oncall_critical
    
  - name: HighTimeoutRate
    condition: timeout_rate > 0.10
    duration: 10m
    action: notify_team
    
  - name: InvalidResponses
    condition: parse_error_rate > 0.02
    duration: 5m
    action: notify_team
```

## Testing Error Scenarios

```java
@SpringBootTest
class AiErrorHandlingTest {
    
    @MockBean
    private AiClient aiClient;
    
    @Test
    void testTimeoutFallback() {
        Mockito.doThrow(new TimeoutException())
            .when(aiClient).generateResponse(anyString());
        
        String result = service.search(request);
        
        assertNotNull(result);
        assertTrue(result.isFallback());
    }
    
    @Test
    void testInvalidResponseHandling() {
        Mockito.when(aiClient.generateResponse(anyString()))
            .thenReturn("{invalid json}");
        
        assertThrows(InvalidResponseException.class, 
            () -> service.search(request));
    }
    
    @Test
    void testMultiProviderFailover() {
        when(openaiClient.generateResponse(anyString()))
            .thenThrow(new TimeoutException());
        when(claudeClient.generateResponse(anyString()))
            .thenThrow(new TimeoutException());
        when(ollamaClient.generateResponse(anyString()))
            .thenReturn("success");
        
        String result = multiProviderClient.generateResponse(prompt);
        
        assertEquals("success", result);
    }
}
```

---

**Key Principle:** Never let AI failures crash the service. Always have a fallback.


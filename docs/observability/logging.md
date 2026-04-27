# Structured Logging for AI Systems

## Log Levels

```
DEBUG:  Token counts, prompt details (dev only)
INFO:   API calls, cache hits/misses
WARN:   Fallbacks, timeouts, invalid responses
ERROR:  API failures, parsing failures
```

## Structured Log Format

```json
{
  "timestamp": "2024-04-27T10:30:45.123Z",
  "traceId": "abc-123-def",
  "feature": "product-search",
  "level": "INFO",
  "message": "AI call completed",
  "aiCall": {
    "model": "gpt-3.5-turbo",
    "inputTokens": 350,
    "outputTokens": 45,
    "latencyMs": 234,
    "cost": 0.0009,
    "cached": false
  },
  "request": {
    "userId": "user-123",
    "query": "comfortable laptop"
  },
  "response": {
    "status": "success",
    "resultCount": 10
  }
}
```

## Implementation

```java
@Service
public class ProductSearchService {
    
    private final Logger log = LoggerFactory
        .getLogger(ProductSearchService.class);
    
    public void search(ProductSearchRequest req) {
        Map<String, Object> context = new HashMap<>();
        context.put("feature", "product-search");
        context.put("userId", req.getUserId());
        context.put("query", req.getQuery());
        
        try {
            // Search
            List<Product> results = db.search(req);
            
            // AI call
            long start = System.currentTimeMillis();
            String aiResponse = aiClient.generateResponse(buildPrompt(results));
            long latency = System.currentTimeMillis() - start;
            
            // Log structured
            Map<String, Object> aiLog = new HashMap<>();
            aiLog.put("model", "gpt-3.5");
            aiLog.put("latencyMs", latency);
            aiLog.put("cached", false);
            context.put("aiCall", aiLog);
            
            log.info("AI call completed", context);
            
        } catch (Exception e) {
            context.put("error", e.getMessage());
            log.error("Search failed", context, e);
        }
    }
}
```

## Log Aggregation

**Tools:** DataDog, Splunk, CloudWatch, ELK

**Key queries:**
```
# Find slow AI calls
feature:product-search AND aiCall.latencyMs > 5000

# Find expensive calls
feature:* AND aiCall.cost > 0.01

# Find fallbacks
level:WARN AND fallback:true

# Cost per feature
feature:* | stats sum(aiCall.cost) by feature
```

---

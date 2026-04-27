# Performance Optimization Skill

## Purpose
This skill covers caching strategies, async processing, batching, and optimization techniques.

## 1. Caching Strategy (Biggest ROI)

### Three-Level Cache
```
L1: In-Memory (Caffeine)
├─ Hit time: <1ms
├─ Typical hit rate: 30-40%
├─ Size: 10K entries

L2: Redis (Distributed)
├─ Hit time: <10ms
├─ Typical hit rate: 60-70%
├─ Size: 1M entries

L3: Database
├─ Hit time: <100ms
├─ Persistent storage
├─ Size: unlimited
```

### Cache Key Strategy
```java
// Good: Semantically similar queries → same response
hashKey("comfortable laptop") == hashKey("laptop comfortable")

// Bad: Exact match only
"laptop for programming" != "laptop for coding"
```

### Cache Invalidation
```
Strategy 1: TTL (Time To Live)
- Default: 24 hours
- Documents: 12 hours (change frequently)
- User profiles: 1 hour (personalized)

Strategy 2: Event-based
- Product updated → invalidate search cache
- Recommendation updated → invalidate rec cache

Strategy 3: Manual
- Admin can force refresh
- Used for critical data
```

## 2. Async Processing

### When to Use
```
SYNC:
├─ User is waiting for response
├─ <500ms latency required
└─ Example: Product search endpoint

ASYNC:
├─ User doesn't need immediate result
├─ Can notify later (email, notification)
└─ Example: Background enrichment, analytics
```

### Implementation Pattern
```java
@Service
public class AsyncAiEnhancement {
    
    public Response retrieveImmediately(Request req) {
        // Return database result immediately
        List<Product> immediate = db.search(req);
        
        // Schedule AI ranking in background
        CompletableFuture.supplyAsync(() -> {
            enrichWithAi(immediate);
        });
        
        return Response.builder()
            .results(immediate)
            .aiEnhanced(false)
            .willEnhance(true)  // Hint: refresh in 2s
            .build();
    }
}
```

## 3. Batching (Request Combining)

### Benefits
- 30-50% cost reduction
- Better throughput
- Less network overhead

### Trade-off
- Higher latency (batch window)

### Implementation
```java
@Service
public class BatchedAiService {
    
    private BlockingQueue<Request> queue = 
        new LinkedBlockingQueue<>(10000);
    
    public CompletableFuture<String> scheduleRequest(String prompt) {
        CompletableFuture<String> future = new CompletableFuture<>();
        queue.add(new Request(prompt, future));
        return future;
    }
    
    @Scheduled(fixedDelay = 100)  // Process every 100ms
    public void processBatch() {
        List<Request> batch = new ArrayList<>();
        queue.drainTo(batch, 100);  // Batch up to 100
        
        // Call API once with all prompts
        List<String> results = aiClient.batchGenerate(batch);
        
        for (int i = 0; i < batch.size(); i++) {
            batch.get(i).future.complete(results.get(i));
        }
    }
}
```

## 4. Connection Pooling

### Configuration
```yaml
http:
  pool:
    maxTotal: 100
    perRoute: 50
    keepAlive: true
    socketTimeout: 30000
```

## Cost Impact Summary

| Optimization | Cost Reduction | Latency Impact | Complexity |
|---|---|---|---|
| Caching | 40-60% | Improvement | Medium |
| Batching | 20-30% | +200ms batch | High |
| Cheaper model | 70% | +5% quality loss | Low |
| Async | 0% | +1s user wait | Medium |
| Load shedding | 50%+ | Graceful degrade | Low |

## When to Optimize
1. If cost > $100/day: Implement caching
2. If latency > 1s p95: Use async or cheaper model
3. If throughput > 100 req/s: Use batching
4. If quality regression: A/B test cheaper model

## Monitoring
Track these metrics:
- Cache hit rate (target: >50%)
- P95 latency (target: <500ms)
- Cost per request (alert if increasing)
- Queue size (alert if backing up)

---

**Next**: See `prompts/optimize-performance.md` for step-by-step instructions

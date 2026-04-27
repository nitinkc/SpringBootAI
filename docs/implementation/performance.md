# Performance Optimization

## 1. Caching Strategy

**Three-Level Cache:**

```java
@Service
public class CachedAiClient implements AiClient {
    
    private final AiClient delegate;
    private final ConcurrentHashMap<String, String> l1;    // In-memory
    private final RedisTemplate<String, String> l2;        // Redis
    private final DatabaseCache l3;                         // Database
    
    @Override
    public String generateResponse(String prompt) {
        String key = hashPrompt(prompt);
        
        // Try L1 cache (in-process, <1ms)
        String cached = l1.get(key);
        if (cached != null) {
            metrics.counter("cache.hit.l1").increment();
            return cached;
        }
        
        // Try L2 cache (Redis, <10ms)
        cached = l2.opsForValue().get(key);
        if (cached != null) {
            metrics.counter("cache.hit.l2").increment();
            l1.put(key, cached);  // Populate L1
            return cached;
        }
        
        // Try L3 cache (DB, <100ms)
        cached = l3.get(key);
        if (cached != null) {
            metrics.counter("cache.hit.l3").increment();
            l2.opsForValue().set(key, cached);  // Populate L2
            l1.put(key, cached);
            return cached;
        }
        
        // Cache miss - call AI
        metrics.counter("cache.miss").increment();
        String result = delegate.generateResponse(prompt);
        
        // Store in all caches
        l1.put(key, result);
        l2.opsForValue().set(key, result, Duration.ofHours(24));
        l3.put(key, result);
        
        return result;
    }
}
```

**Cache Hit Strategy:**
```
Goal: 60% cache hit rate

Tactics:
1. Semantic caching: Similar queries → same response
2. Bucketing: Round timestamp to hour (same request at 14:05 and 14:35)
3. Normalization: "best laptop" = "laptop recommendations"
```

## 2. Batch Processing

**Batch Multiple Requests:**

```java
@Service
public class BatchedAiService {
    
    private final BlockingQueue<AiRequest> queue = 
        new LinkedBlockingQueue<>(1000);
    
    public void scheduleWithBatch(String prompt) {
        queue.add(new AiRequest(prompt));
    }
    
    @Scheduled(fixedDelay = 100, initialDelay = 100)
    public void processBatch() {
        List<AiRequest> batch = new ArrayList<>();
        queue.drainTo(batch, 100);  // Get up to 100
        
        if (batch.isEmpty()) return;
        
        // Process all at once (more efficient)
        List<String> results = aiClient.generateResponseBatch(
            batch.stream().map(r -> r.prompt).collect(toList())
        );
        
        for (int i = 0; i < batch.size(); i++) {
            batch.get(i).future.complete(results.get(i));
        }
    }
}
```

## 3. Async Pattern

```java
@Service
public class AsyncAiEnhancement {
    
    public ProductSearchResponse searchAsync(ProductSearchRequest req) {
        // Immediately return database results
        List<Product> immediate = db.search(req);
        
        // Schedule AI ranking in background
        CompletableFuture<List<Product>> aiRanking = 
            CompletableFuture.supplyAsync(() -> 
                enhanceWithAi(immediate)
            );
        
        // Return response without waiting
        return ProductSearchResponse.builder()
            .results(immediate)
            .aiEnhanced(false)
            .estimatedAiTime(500)  // Hint to client
            .build();
    }
}
```

## 4. Compression

```java
@Service
public class CompressedAiClient {
    
    public String generateResponse(String prompt) {
        // Compress prompt for transmission
        byte[] compressed = compress(prompt.getBytes());
        
        // Send to AI service
        ResponseEntity<String> response = rest.postForEntity(
            "/api/generate",
            compressed,
            String.class,
            new HttpHeaders() {{
                set("Content-Encoding", "gzip");
            }}
        );
        
        return response.getBody();
    }
}
```

## 5. Connection Pooling

```java
@Configuration
public class AiClientConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        HttpClientHttpRequestFactory factory = 
            new HttpClientHttpRequestFactory();
        
        HttpClient httpClient = HttpClientBuilder.create()
            .setMaxConnTotal(100)           // Total connections
            .setMaxConnPerRoute(50)         // Per route
            .setConnectionReuseStrategy((request, response, context) -> true)
            .build();
        
        factory.setHttpClient(httpClient);
        return new RestTemplate(factory);
    }
}
```

## Performance Targets

```
Service Layer AI Performance:

Database query:           10ms
Build context:           10ms
AI API call:            200ms
Parse response:          10ms
Merge with catalog:      20ms
─────────────────────────────
Total latency:         ~250ms

Target p95:            <500ms
Target p99:            <2000ms
```

---

[Next: Error Handling](error-handling.md)

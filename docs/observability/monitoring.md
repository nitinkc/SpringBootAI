# Observability: Monitoring & Metrics

## Why Monitor AI Features?

AI features introduce new unknowns:
- **Latency**: LLM calls add 1-5 seconds
- **Cost**: Each call costs money
- **Quality**: Output varies, may be wrong
- **Availability**: LLM service may go down
- **Tokens**: Hidden complexity (tokens != requests)

**Without observability, you're flying blind.**

## Key Metrics to Track

### 1. Performance Metrics

```java
// In your service layer
Supplier<ProductSearchResponse> timedCall = () -> {
    long start = System.currentTimeMillis();
    try {
        return performSearch(request);
    } finally {
        long duration = System.currentTimeMillis() - start;
        metrics.recordSearchDuration(duration);
    }
};
```

**What to track:**
- **E2E Latency**: Total request time (50ms - 5000ms)
- **LLM Latency**: Just LLM call (1000ms - 5000ms)
- **DB Latency**: Just database (10ms - 100ms)
- **P50/P95/P99**: Percentiles matter
- **Timeout Rate**: % of requests that timeout

**Why:**
- Detects performance degradation
- SLA violations (if you promise <2s)
- Identifies bottlenecks

### 2. Cost Metrics

```java
// Track actual token usage
public class AiCostTracker {
    
    public void recordTokenUsage(String model, int inputTokens, int outputTokens) {
        double cost = calculateCost(model, inputTokens, outputTokens);
        metrics.recordAiCost(cost);
        metrics.recordInputTokens(inputTokens);
        metrics.recordOutputTokens(outputTokens);
    }
    
    private double calculateCost(String model, int input, int output) {
        return switch (model) {
            case "gpt-4" -> (input * 0.00003) + (output * 0.00006);
            case "gpt-3.5" -> (input * 0.0000005) + (output * 0.0000015);
            default -> 0;
        };
    }
}
```

**What to track:**
- **Cost per request**: dollars spent
- **Total monthly cost**: budget tracking
- **Cost per feature**: feature-level attribution
- **Token efficiency**: are we using tokens efficiently?

**Why:**
- AI is expensive: ~$0.01-0.10 per request
- Budget planning
- ROI calculation

### 3. Quality Metrics

```java
// Track LLM output quality
public class AiQualityMetrics {
    
    public void recordSearchEnhancement(
        List<Product> originalResults,
        List<Product> enhancedResults,
        String aiSuggestion) {
        
        // Did AI change the results?
        if (!originalResults.equals(enhancedResults)) {
            metrics.recordAiImpact("changed_results");
        }
        
        // User feedback (if available)
        metrics.recordUserSatisfaction(aiSuggestion);
        
        // Response parsing success
        if (isValidAiResponse(aiSuggestion)) {
            metrics.recordParsingSuccess();
        } else {
            metrics.recordParsingFailure();
        }
    }
}
```

**What to track:**
- **Parsing success rate**: % of valid responses
- **User satisfaction**: thumbs up/down on results
- **Impact measurement**: Did AI improve results?
- **Error types**: Parse errors, invalid output, etc.

**Why:**
- Is the AI feature actually helping?
- Inform decisions about using AI
- Identify bad prompts or quality issues

### 4. Availability Metrics

```java
// Track LLM availability
public class AiAvailabilityMetrics {
    
    public void recordAiCall(boolean success, Exception error) {
        if (success) {
            metrics.recordAiSuccess();
        } else {
            String errorType = error.getClass().getSimpleName();
            metrics.recordAiFailure(errorType);
        }
    }
    
    // Track fallback usage
    public void recordFallbackUsed(String reason) {
        metrics.recordFallback(reason);  // "timeout", "rate_limit", "unavailable"
    }
}
```

**What to track:**
- **Success rate**: % of successful AI calls
- **Error types**: Timeouts, auth failures, rate limits
- **Fallback rate**: % using traditional method
- **SLA uptime**: LLM provider availability

**Why:**
- Understand system reliability
- Ensure fallbacks are working
- Alert on provider issues

## Implementing Observability

### Using Micrometer (Spring Boot)

```java
@Service
public class ProductSearchService {
    
    private final MeterRegistry meterRegistry;
    private final AiClient aiClient;
    
    public ProductSearchResponse searchProducts(ProductSearchRequest request) {
        return meterRegistry.timer("product.search").recordCallable(() -> {
            
            // Start metrics
            long startTime = System.nanoTime();
            
            try {
                // Actual search logic
                List<Product> results = findProducts(request);
                
                if (request.getUseAIEnhancement()) {
                    meterRegistry.timer("ai.search.duration")
                        .record(() -> enhanceWithAI(results));
                    
                    meterRegistry.counter("ai.search.success").increment();
                }
                
                return buildResponse(results);
                
            } catch (TimeoutException e) {
                meterRegistry.counter("ai.search.timeout").increment();
                return getFallbackResponse();
            } catch (Exception e) {
                meterRegistry.counter("ai.search.error").increment();
                throw e;
            }
        });
    }
}
```

### Custom Metrics

```java
@Service
public class AiMetricsService {
    
    private final MeterRegistry meterRegistry;
    
    public AiMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Create custom gauges
        Gauge.builder("ai.request.queue.size", this::getQueueSize)
            .register(meterRegistry);
            
        Gauge.builder("ai.monthly.cost", this::calculateMonthlyCost)
            .register(meterRegistry);
    }
    
    public void recordLlmCall(String model, int inputTokens, int outputTokens, long durationMs) {
        // Latency
        meterRegistry.timer("llm.call.duration", "model", model)
            .record(durationMs, TimeUnit.MILLISECONDS);
        
        // Tokens
        meterRegistry.counter("llm.tokens.input", "model", model)
            .increment(inputTokens);
        meterRegistry.counter("llm.tokens.output", "model", model)
            .increment(outputTokens);
        
        // Cost
        double cost = calculateCost(model, inputTokens, outputTokens);
        meterRegistry.counter("llm.cost.usd", "model", model)
            .increment(cost);
    }
}
```

## Dashboards

### Prometheus Queries

```promql
# Latency: P95 of LLM calls
histogram_quantile(0.95, rate(llm_call_duration_seconds[5m]))

# Success Rate
rate(llm_call_success_total[5m]) / rate(llm_call_total[5m])

# Monthly Cost (estimate)
rate(llm_cost_usd_total[30d]) * 30 * 24 * 60 * 60

# Cache Hit Rate
rate(ai_cache_hits_total[5m]) / rate(ai_cache_requests_total[5m])

# Fallback Rate
rate(ai_fallback_total[5m]) / rate(ai_request_total[5m])
```

### Grafana Dashboard Panels

```
┌─────────────────────────────────────────┐
│           AI Feature Metrics             │
├──────────────────┬──────────────────────┤
│ LLM Latency (P95)│ Error Rate            │
│   2.1 seconds    │  3.2%                 │
├──────────────────┼──────────────────────┤
│ Cost/Hour        │ Cache Hit Rate        │
│   $4.25          │  52%                  │
├──────────────────┼──────────────────────┤
│ Token Usage/Hour │ Fallback Rate         │
│   45K tokens     │  1.8%                 │
├──────────────────┼──────────────────────┤
│    Availability   │  Success Rate        │
│    99.8%         │  96.8%                │
└──────────────────┴──────────────────────┘
```

## Alerts You Need

```yaml
# alert.yml
groups:
  - name: ai_alerts
    rules:
      # Performance
      - alert: LlmLatencyHigh
        expr: histogram_quantile(0.95, llm_call_duration_seconds) > 5
        for: 5m
        annotations:
          summary: "LLM P95 latency > 5 seconds"
      
      # Cost
      - alert: MonthlyCostExceeded
        expr: llm_monthly_cost_estimate > 10000
        for: 1h
        annotations:
          summary: "Estimated monthly cost > $10k"
      
      # Availability
      - alert: LlmServiceDown
        expr: rate(llm_call_error_total[5m]) > 0.1
        for: 5m
        annotations:
          summary: "LLM error rate > 10%"
      
      # Cache
      - alert: LowCacheHitRate
        expr: cache_hit_rate < 0.3
        for: 1h
        annotations:
          summary: "Cache hit rate < 30%, check query patterns"
```

## Structured Logging

```java
// Log with context for analysis
Logger logger = LoggerFactory.getLogger(ProductSearchService.class);

public ProductSearchResponse search(ProductSearchRequest request) {
    String traceId = UUID.randomUUID().toString();
    MDC.put("trace_id", traceId);
    MDC.put("feature", "product_search");
    
    logger.info("Search started",
        "query", request.getQuery(),
        "trace_id", traceId);
    
    try {
        long start = System.nanoTime();
        String aiResponse = aiClient.generateResponse(buildContext(request));
        long duration = System.nanoTime() - start;
        
        logger.info("AI call successful",
            "duration_ms", TimeUnit.NANOSECONDS.toMillis(duration),
            "tokens_estimated", estimateTokens(context));
        
        return buildResponse(aiResponse);
        
    } catch (Exception e) {
        logger.error("AI call failed",
            "error", e.getMessage(),
            "error_type", e.getClass().getSimpleName());
        throw e;
    } finally {
        MDC.clear();
    }
}
```

### Logs Should Include
- `trace_id`: Correlate requests across services
- `duration`: How long the operation took
- `tokens`: Tokens used (impacts cost)
- `error_type`: Kind of error for debugging
- `user_id`: Which user (for debugging)
- `model`: Which LLM provider was used
- `cache_hit`: Was result cached?

---

Next: [Logging Strategy](logging.md) →

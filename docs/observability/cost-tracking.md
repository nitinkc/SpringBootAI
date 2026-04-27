# Cost Tracking & Optimization

## Real-Time Cost Calculation

Every LLM call has a cost that must be tracked:

```
Cost = (Input Tokens × Input Price) + (Output Tokens × Output Price)
```

### Provider-Specific Pricing

**OpenAI (GPT-3.5 Turbo):**
- Input: $0.005 per 1K tokens
- Output: $0.0015 per 1K tokens

**Anthropic (Claude 3 Sonnet):**
- Input: $0.003 per 1K tokens
- Output: $0.015 per 1K tokens

**Self-Hosted (Ollama):**
- Cost: $0 (infrastructure cost only)

## Implementation

```java
@Service
public class AiCostTracker {
    
    private final MeterRegistry metrics;
    
    public void trackApiCall(String feature, String model, 
                            int inputTokens, int outputTokens, 
                            long durationMs) {
        
        // Calculate cost
        double cost = calculateCost(model, inputTokens, outputTokens);
        
        // Record metrics
        metrics.counter("ai.cost.usd", 
            "feature", feature, 
            "model", model)
            .increment(cost);
        
        // Track tokens
        metrics.counter("ai.tokens.input", "model", model)
            .increment(inputTokens);
        metrics.counter("ai.tokens.output", "model", model)
            .increment(outputTokens);
        
        // Track latency
        metrics.timer("ai.latency", "feature", feature)
            .record(durationMs, TimeUnit.MILLISECONDS);
    }
    
    private double calculateCost(String model, int input, int output) {
        return switch (model) {
            case "gpt-3.5" -> (input * 0.000005) + (output * 0.0000015);
            case "gpt-4" -> (input * 0.00003) + (output * 0.00006);
            case "claude-3-sonnet" -> (input * 0.000003) + (output * 0.000015);
            default -> 0;
        };
    }
}
```

## Monthly Cost Estimation

### Example: Product Search Feature

```
Metrics:
- 100K searches/month
- Average: 300 input tokens, 50 output tokens
- Model: GPT-3.5
- Cache hit rate: 50% (50K requests skip LLM)

Calculation:
- 50K API calls × 300 tokens × $0.000005 = $0.75
- 50K API calls × 50 tokens × $0.0000015 = $0.04
Total: ~$0.79/month (with caching)

Without caching:
- 100K API calls = $1.58/month
```

## Cost Optimization Strategies

### 1. Caching (30-60% savings)
```java
// Highest ROI optimization
String response = cache.getOrCompute(key, 
    () -> aiClient.generateResponse(prompt));
```

### 2. Cheaper Model (40-80% savings)
```yaml
ai:
  model: gpt-3.5-turbo  # $0.001 per request
  # vs gpt-4 at $0.03 per request
```

### 3. Token Optimization (10-30% savings)
```java
// Shorter prompts use fewer tokens
String shortPrompt = buildCompactContext(request);  // vs verbose
```

### 4. Load Shedding (10-50% savings)
```java
// Skip AI during peak hours or for low-priority features
if (isHighLoad()) {
    return traditionalResponse();
}
```

### 5. Self-Hosting (90% savings)
```yaml
ai:
  provider: ollama
  # Free locally, only infrastructure cost
```

## Alerting

```yaml
alerts:
  - name: daily_cost_threshold
    condition: daily_cost > $100
    action: notify_team
  
  - name: monthly_forecast
    condition: (daily_cost × 30) > monthly_budget
    action: trigger_optimization
```

---

**Next**: Learn [Performance Optimization](performance.md)

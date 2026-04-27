# Interview Preparation: Observability Questions

## Q1: How would you monitor an AI system in production?

### Answer: Multi-Level Observability

**Level 1: Metrics (Quantifiable)**
- Latency: P50, P95, P99
- Cost: per request, per hour, per month
- Quality: parsing success rate
- Availability: error rate, fallback rate

**Level 2: Logging (Detailed)**
- Trace ID: correlate across services
- Tokens used: cost attribution
- Error details: type, cause, solution
- User context: for debugging

**Level 3: Tracing (End-to-End)**
- Request flow visualization
- Bottleneck identification
- Service dependencies

**Level 4: Analytics (Patterns)**
- Cost trends
- Latency percentile trends
- Quality regression detection

## Q2: Critical alerts you'd set?

### Answer

| Metric | Threshold | Impact |
|--------|-----------|--------|
| P95 Latency | > 5s | Users experiencing slow/timeout |
| Error Rate | > 5% | System degraded |
| Daily Cost | > 1.5x normal | Cost overrun |
| Cache Hit | < 30% | Wasting money |
| Fallback Rate | > 10% | AI provider issues |

```yaml
alerts:
  - alert: HighLatency
    condition: histogram_quantile(0.95, latency) > 5000ms
    duration: 5m
    action: page_oncall
  
  - alert: HighErrorRate
    condition: error_rate > 0.05
    duration: 5m
    action: page_oncall
  
  - alert: CostOverrun
    condition: daily_cost > budget * 1.5
    duration: 1h
    action: notify_team
```

## Q3: How to track quality?

### Answer

**Automated Metrics:**
- Response parsing success rate
- Schema validation success
- Known pattern matching

**User Feedback:**
```java
// Collect explicit feedback
public void recordUserFeedback(Long resultId, int rating) {
    metrics.recordRating("result", resultId, rating);
}

// Implicit feedback
// - Click-through rate
// - Conversion rate
// - Return rate
```

**Cohort Analysis:**
- Did AI improve results for 80% of users?
- Which use cases benefit most?
- Where is quality poor?

## Q4: Cost tracking in detail?

### Answer

```java
public void trackLlmCall(AiCallEvent event) {
    // Per-request level
    double cost = event.inputTokens * pricePerInputToken 
               + event.outputTokens * pricePerOutputToken;
    
    metrics.recordCost(cost, 
        "feature", event.feature,
        "model", event.model);
    
    // Aggregate levels
    dailyCost += cost;
    monthlyCost += cost;
    featureCost[event.feature] += cost;
}
```

**Visibility:**
- Per-request cost (granular)
- Per-feature cost (feature-level ROI)
- Hourly cost (trends)
- Daily cost (budget tracking)
- Monthly cost (financial planning)

## Q5: How to optimize based on observability?

### Answer

**Optimize Based on Data:**

1. **Low Cache Hit Rate** → Improve caching strategy
2. **High Latency** → Use faster model or caching
3. **High Cost** → Implement caching or cheaper model
4. **Low Quality** → Improve prompts or use better model
5. **High Error Rate** → Improve error handling

**Closed Loop:**
```
Observe metrics
    ↓
Identify problem
    ↓
Hypothesize solution
    ↓
Implement change
    ↓
Observe impact
    ↓
Validate improvement
```

---

Next: [Behavioral Questions](behavioral.md)

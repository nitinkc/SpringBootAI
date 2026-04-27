# Interview Preparation: Architecture Questions

## Q1: Walk us through the architecture of your AI-enhanced system

### Model Answer Structure

**Start with the big picture:**
> "Our architecture integrates AI into the service layer without changing the overall system design. Here's the flow:"

```
HTTP Request
    ↓
Controller (REST endpoint)
    ↓
Service Layer (our innovation):
  ├─ Fetch data from database
  ├─ Build AI context from that data
  ├─ Call LLM via abstracted AiClient
  ├─ Validate/process LLM response
  └─ Enhance original data
    ↓
Repository Layer (unchanged)
    ↓
Response with AI enhancements
```

**Then explain the key components:**

1. **Why Service Layer?**
   - Single Responsibility: Service = business logic
   - Has access to all data needed
   - Easy to test (mock AI client)
   - Reusable across multiple endpoints
   - No system redesign needed

2. **AiClient Abstraction**
   ```java
   public interface AiClient {
       String generateResponse(String prompt);
       String generateResponseWithContext(String system, String user);
       boolean isAvailable();
       String getModelName();
   }
   ```
   - Allows swapping providers (OpenAI → Claude)
   - Supports mocking for tests
   - Single point of change for LLM logic

3. **Concrete Implementations**
   - MockAiClient (development, testing)
   - OpenAiClient (production GPT-4/3.5)
   - AnthropicClient (alternative)
   - OllamaClient (self-hosted)

**Key Decision:**
> "We made AI optional per-request. Service checks if user wants AI enhancement and whether AI client is available before calling. This ensures backwards compatibility and graceful degradation."

---

## Q2: How does your system handle AI failures?

### Good Answer

> "We have multiple layers of failure handling:"

**Level 1: Availability Check**
```java
if (!aiClient.isAvailable()) {
    return traditionalResponse();
}
```

**Level 2: Timeout Protection**
```java
try {
    return aiClient.generateResponseWithTimeout(prompt, Duration.ofSeconds(3));
} catch (TimeoutException) {
    metrics.recordTimeout();
    return cachedOrTraditionalResponse();
}
```

**Level 3: Invalid Response Handling**
```java
String response = aiClient.generateResponse(prompt);
if (!isValidResponse(response)) {
    logger.warn("Invalid AI response received");
    metrics.recordInvalidResponse();
    return fallbackResponse();
}
```

**Level 4: Monitoring & Alerting**
- Track error rate per provider
- Alert if error rate > 5%
- Automatic fallback to traditional method
- Log all errors with context for debugging

**Key Point:**
> "The user never sees an error. If AI fails, they get high-quality traditional results instead. AI is an enhancement, not a requirement."

---

## Q3: How do you decide when to use AI?

### Product Search Example

> "Not every use case benefits from AI. For product search, here's our decision matrix:"

| Scenario | Use AI? | Reasoning |
|----------|---------|-----------|
| User searches "fast laptop" | ✅ Yes | Needs understanding of "fast" in context |
| User searches exact product code | ❌ No | Index lookup is faster and more accurate |
| Similar searches within 5 minutes | ❌ No (use cache) | Cache is faster and cheaper |
| 100+ matching products | ✅ Yes | AI can rank by relevance |
| 0 products found | ❌ No | Nothing to analyze |

> "We use a decision tree in the service:"

```java
public boolean shouldEnhanceWithAI(SearchRequest req, List<Product> results) {
    // No enhancement if not requested
    if (!req.getUseAIEnhancement()) return false;
    
    // Need results to analyze
    if (results.isEmpty()) return false;
    
    // Check LLM availability
    if (!aiClient.isAvailable()) return false;
    
    // Check rate limiting
    if (rateLimiter.isExceeded()) return false;
    
    // Check cost budget
    if (monthlySpend.exceedsBudget()) return false;
    
    return true;
}
```

**Criteria we consider:**
1. **Cost-Benefit**: Will AI improve results enough to justify cost?
2. **Latency**: Is 2-5s acceptable for this use case?
3. **Data Quality**: Do we have enough context for good prompt?
4. **Fallback**: Can we gracefully degrade to non-AI?
5. **Accuracy**: Is AI output usually correct?

---

## Q4: Tell us about your caching strategy

### Answer

> "We implemented multi-level caching because LLM calls are expensive:"

**Level 1: Full Response Caching**
```java
@Cacheable(cacheNames = "productSearchCache", key = "#request.query")
public ProductSearchResponse search(SearchRequest request) {
    // First call hits DB + LLM
    // Second identical call returns cached result
}
```
- **Hit Rate**: 30-50% for product search
- **Benefit**: 30-50% cost reduction
- **Trade-off**: Cache invalidation complexity

**Level 2: Context Caching**
```java
String context = contextCache.getOrCompute(
    cacheKey(request),
    () -> buildContext(request)
);

String response = aiClient.generateResponse(context);
```
- **Benefit**: Avoid rebuilding context multiple times
- **Hit Rate**: 40-60%

**Level 3: Semantic Caching** (in progress)
- Cache embeddings of user queries
- Find similar cached queries
- Return similar response (might adapt slightly)
- Higher hit rate (60-80%)
- More complex to implement

**Monitoring Cache Effectiveness:**
```promql
# Cache hit rate should be > 40%
cache_hits / (cache_hits + cache_misses)

# If < 40%, consider:
# - Adjusting cache TTL
# - Improving cache key strategy
# - Adding semantic caching
```

---

## Q5: How do you track AI costs?

### Answer

> "Cost tracking is critical because AI isn't free. We track at multiple levels:"

**Request Level:**
```java
public void recordAiCall(String feature, String model, int inputTokens, int outputTokens) {
    double cost = tokensToCost(model, inputTokens, outputTokens);
    metrics.recordFeatureCost(feature, cost);
    logger.info("AI call", 
        "feature", feature,
        "model", model,
        "cost", String.format("$%.4f", cost));
}
```

**Feature Level:**
```
Product Search:  $2,150/month  (50% cached, saved $2,150)
Support Tickets: $1,200/month  (no caching, each unique)
Recommendations: $800/month    (40% cached)
─────────────────────────────
Total:          $4,150/month
```

**Monthly Trends:**
- Plot cumulative cost
- Alert if trending > budget
- Identify high-cost features
- Optimize expensive features first

**Cost Optimization Actions:**
1. Cache more (biggest ROI usually)
2. Use cheaper model (GPT-3.5 vs GPT-4)
3. Optimize prompts (fewer tokens)
4. Load shedding under high load
5. Self-host with Ollama (90% cost reduction)

---

## Q6: What trade-offs did you make?

### Answer

> "Every architectural decision involves trade-offs. Here are ours:"

| Decision | Chose | Benefit | Trade-off |
|----------|-------|---------|-----------|
| **Service Layer AI** | Service Layer | Easy to test, no redesign | Slightly slower than controller |
| **Abstraction** | Interface | Swap providers easily | Small overhead |
| **Sync calls** | Sync | Simpler, deterministic | 1-5s latency |
| **Caching** | Full + Context | Cost savings | Invalidation complexity |
| **Fallback** | Traditional | Reliability | More code |
| **Optional AI** | Per-request opt-in | No breaking changes | User must request AI |

**Trade-offs we specifically justified:**

**Latency vs Quality:**
> "We accept 1-5s latency for search enhancement because the quality improvement justifies it. For user-facing pages, we'd use async or caching instead."

**Cost vs Accuracy:**
> "We use GPT-3.5 (80% accuracy, $0.001/request) instead of GPT-4 (95% accuracy, $0.03/request) because cost is 30x higher for minimal improvement in product search context."

**Complexity vs Flexibility:**
> "We chose abstraction via AiClient interface even though it adds factory code, because it means we can swap OpenAI → Claude in config, not code. This flexibility justifies the complexity."

---

## Q7: How would you scale this to 1M requests/day?

### Answer

> "Scaling AI introduces new challenges. Here's our roadmap:"

**Current (10K requests/day):**
- Single LLM provider (OpenAI)
- Basic caching (full responses)
- No rate limiting needed

**Target (1M requests/day = 1000x):**

1. **Multi-Provider Load Balancing**
   ```
   Request load:
   ├─ 50% → OpenAI (best quality)
   ├─ 30% → Claude (cost balance)
   └─ 20% → Ollama self-hosted (cost savings)
   ```

2. **Intelligent Caching**
   - Semantic caching (identify similar queries)
   - Embedding-based deduplication
   - Target: 70% cache hit rate (reduce cost 70%)

3. **Request Queueing**
   - Queue requests during peak hours
   - Process 100 requests/second
   - Async processing for non-critical features

4. **Self-Hosting**
   - Deploy Ollama locally
   - Reduce cost 90% (free instead of $0.005/request)
   - Trade-off: Slower (5-10s vs 2-3s)

5. **Feature Prioritization**
   - High priority (search): Always use AI
   - Medium priority (recommendations): Use AI 80% of time
   - Low priority (categorization): Use AI in background only

**Cost Analysis:**

```
1M requests/day × $0.005/request = $5,000/day = $150,000/month (unoptimized)

With optimizations:
- 60% cache hit (40% of requests need LLM) = 400K requests
- 50% moved to cheaper provider = 200K OpenAI + 200K Claude
- Cost = (200K × $0.005) + (200K × $0.003) = $1,000 + $600 = $1,600/day = $48,000/month
- Savings: 68% of cost
```

---

## Q8: What monitoring would you add?

### Answer

> "Observability is essential for AI systems because costs and quality are invisible."

**Metrics:**
- **Latency**: P50, P95, P99 (target: P95 < 3s)
- **Cost**: Cost per request, feature, hour
- **Quality**: Parsing success rate, user feedback
- **Availability**: Error rate, fallback rate
- **Efficiency**: Cache hit rate, tokens/request

**Dashboards:**
- Real-time cost tracking
- Latency trends
- Error rate alert
- Cache effectiveness
- Provider performance comparison

**Alerts:**
- P95 latency > 5 seconds
- Error rate > 5%
- Daily cost > budget
- Cache hit rate < 30%

---

## Q9: What if the LLM provider goes down?

### Answer

> "We built the system to survive provider outages:"

**Architecture Resilience:**
1. **Abstraction**: Easy to switch providers in config
2. **Fallback**: Non-AI results available immediately
3. **Caching**: Recent responses available from cache
4. **Multi-provider**: Load balanced across OpenAI + Claude + Ollama

**Scenario: OpenAI API down**
```
Request arrives
  ↓
Service tries OpenAI (fails, times out)
  ↓
Catches exception, tries Claude (success!)
  ↓
Response returned in ~3 seconds
  ↓
User doesn't notice outage
```

**Scenario: All providers down**
```
Request arrives
  ↓
All AI providers fail
  ↓
Service catches exception
  ↓
Returns cached response (if available) or traditional response
  ↓
User sees quality degradation, not failure
```

---

## Q10: How would you measure ROI of AI?

### Answer

> "ROI is hard to measure but essential to justify costs:"

**Quantifiable Metrics:**
1. **User Engagement**
   - Click-through rate on AI-enhanced results
   - Time spent on results
   - Conversion rate

2. **Cost per Engagement**
   - Cost to service request: $0.005
   - Engagement improvement: 15%
   - Cost per acquisition change: -12%

3. **Business Metrics**
   - Revenue increase: 8% higher
   - Cost reduction: Faster search (fewer queries)
   - Churn reduction: Better results, more satisfaction

**Measurement Method:**
```
A/B Test (week):
├─ Control: 50% users get non-AI results
└─ Test: 50% users get AI-enhanced results

Measure:
├─ Engagement (clicks, time, conversions)
├─ Cost (AI cost vs traditional)
└─ Satisfaction (NPS, ratings)

ROI = (Revenue Gain - AI Cost) / AI Cost
```

**Example Results:**
```
AI Cost: $1,600/day = $48,000/month
Revenue Gain: $75,000/month
ROI = ($75,000 - $48,000) / $48,000 = 56% positive ROI
```

---

Next: [Design Pattern Questions](design-patterns.md) →

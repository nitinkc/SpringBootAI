# Design Decision Matrix

## Decision 1: Where to Integrate AI?

### Options Comparison

| Location | Controllers | Service Layer | Separate Service |
|----------|---|---|---|
| **Responsibility** | HTTP → Business | Business Logic | External AI Service |
| **Data Access** | Via services | Direct | Via services |
| **Cohesion** | Low | High | Low |
| **Reusability** | Per endpoint | Multiple endpoints | Multiple apps |
| **Testability** | Hard | Easy | Medium |
| **Latency** | ~1-5s | ~1-5s | ~2-6s (+RPC) |
| **Complexity** | Medium | High | High |
| **Scaling** | With main app | With main app | Independent |

### Recommendation: Service Layer ✅
**Why:**
- Single Responsibility Principle ✅
- Easy to test ✅
- Reusable across endpoints ✅
- No additional infrastructure ✅
- Best latency ✅

---

## Decision 2: Which LLM Provider?

### Provider Comparison Matrix

| Provider | Model | Cost/1K Tokens | Speed | Accuracy | Customization | Self-Hosted |
|---|---|---|---|---|---|---|
| **OpenAI** | GPT-4 | $0.03 in/$0.06 out | 2-5s | ⭐⭐⭐⭐⭐ | Medium | ❌ |
| **Anthropic** | Claude 3 Opus | $0.015 in/$0.075 out | 2-5s | ⭐⭐⭐⭐⭐ | Medium | ❌ |
| **Google** | Gemini Pro | $0.0005 in/$0.0015 out | 1-3s | ⭐⭐⭐⭐ | Medium | ❌ |
| **Open Source** | Llama 2 (Ollama) | $0 | 1-10s | ⭐⭐⭐ | High | ✅ |
| **Your Project** | GPT-3.5 | $0.0015 in/$0.002 out | 1-3s | ⭐⭐⭐⭐ | Low | ❌ |

### Decision Framework

**Choose based on:**

1. **Cost Sensitivity**
   - High cost sensitivity → Ollama (self-hosted)
   - Medium budget → GPT-3.5 turbo
   - High budget → GPT-4 or Claude Opus

2. **Latency Requirements**
   - <500ms → Can't use LLM, use traditional ML
   - 1-3s tolerance → GPT-3.5, Gemini
   - 5s+ tolerance → GPT-4, Claude Opus

3. **Accuracy Requirements**
   - High accuracy needed → GPT-4, Claude Opus
   - Medium accuracy → GPT-3.5, Claude Sonnet, Gemini
   - Quick answers → Any provider

4. **Data Privacy**
   - Data stays internal → Ollama
   - Can use cloud → Any cloud provider

5. **Expertise**
   - Prompt engineering → Claude or GPT-4
   - Fine-tuning → OpenAI
   - Custom models → Self-hosted Ollama

### Our Demo: MockAiClient
**Rationale:**
- No API costs
- No external dependencies
- Easy to replace with real provider
- Perfect for learning

---

## Decision 3: Caching Strategy

### Caching Options

| Strategy | Implementation | Hit Rate | Latency | Cost Saved |
|---|---|---|---|---|
| **No Cache** | Direct LLM call | N/A | 1-5s | $0 |
| **Full Response** | Cache entire response | 30-60% | 100ms | 30-60% |
| **Semantic Cache** | Smart deduplication | 50-80% | 100ms | 50-80% |
| **Embedding Cache** | Vector similarity | 40-70% | 100ms | 40-70% |

### When to Cache?

| Scenario | Cache? | Reason |
|---|---|---|
| **Product Search** | ✅ Yes | Same queries repeat often |
| **Support Auto-Response** | ❌ No | Each ticket is unique |
| **Recommendations** | ~ Maybe | Cache by user profile hash |
| **Categorization** | ✅ Yes | Similar inputs have similar outputs |
| **Summarization** | ✅ Yes | Same documents repeat |
| **Translation** | ✅ Yes | Same phrases repeat |

### Implementation Decision

```
Latency Critical?
├─ Yes → Use cache (100ms lookup)
├─ No  → Evaluate cost vs quality

Cost High?
├─ Yes → Use cache
├─ No  → Can afford fresh responses

Same Input Likely?
├─ Yes → Cache works well
├─ No  → Cache not helpful
```

---

## Decision 4: Async vs Sync

### Comparison

| Aspect | Sync | Async |
|---|---|---|
| **User Waits** | Yes (1-5s) | No (immediate) |
| **Response Time** | Slow | Fast |
| **Complexity** | Simple | More complex |
| **Testing** | Easy | Harder |
| **Error Handling** | Straightforward | Complex |
| **Cost** | Per-request | Spread over time |

### Decision Tree

```
User Need Immediate Response?
├─ Yes → Sync (simpler, acceptable latency)
├─ No  → Check priority

Is 2-5s Latency Acceptable?
├─ Yes → Sync
├─ No  → Async

Can Processing Happen in Background?
├─ Yes → Async (better UX)
├─ No  → Sync

Is Cost High?
├─ Yes → Async (spread cost)
├─ No  → Async for UX
```

### Timeline Example

**Sync Product Search (blocking):**
```
User clicks search
    ↓ (User waits)
Service fetches products (50ms)
    ↓ (User waits)
Service calls LLM (2-5s)
    ↓ (User waits)
Response returns to user
TOTAL: 2-5.5 seconds ⏱️
```

**Async Categorization (background):**
```
User submits product
    ↓
Service saves product immediately
    ↓ (User sees confirmation)
Response returns (instant) ✅
    ↓
Background: LLM categorizes product
    ↓
Result saved and available for next user
```

---

## Decision 5: Error Handling Strategy

### Fallback Options

| Error Type | Response | Cost |
|---|---|---|
| **Timeout** | Traditional results | ✅ Low cost |
| **Rate Limit** | Cached results or traditional | ✅ Low |
| **Auth Failure** | Traditional results | ~ Medium |
| **Invalid Output** | Traditional results | ✅ Low |
| **Service Down** | Traditional results | ✅ Low |

### Recommended Strategy

```java
public Response search(Request request) {
    try {
        // Try with AI
        if (aiClient.isAvailable()) {
            return aiEnhancedSearch(request);
        }
    } catch (TimeoutException) {
        log.warn("LLM timeout, using fallback");
        metrics.recordLLMFailure("timeout");
    } catch (RateLimitException) {
        log.warn("LLM rate limit, using cache");
        return cachedResults;
    } catch (Exception e) {
        log.error("LLM error", e);
        metrics.recordLLMFailure("error");
    }
    
    // Fallback: Traditional approach
    return traditionalSearch(request);
}
```

**Key Points:**
- Always have a fallback
- Log failures for monitoring
- Record metrics for observability
- Don't let LLM failure break user experience

---

## Decision 6: Monitoring What?

### Metrics to Track

| Metric | Why | Alarm Threshold |
|---|---|---|
| **LLM Latency** | Detect slowdowns | > 5s |
| **LLM Cost/Request** | Budget tracking | > 0.1$ |
| **Error Rate** | System health | > 5% |
| **Cache Hit Rate** | Cost optimization | < 40% (improve caching) |
| **Timeout Rate** | Performance issue | > 2% |
| **Token Usage** | Cost tracking | Trending up? |

### Observability Stack

```
Application
    ↓
Spring Boot Micrometer
    ↓
Metrics (Prometheus)
    ↓
Dashboards (Grafana)
    ↓
Alerts (Pagerduty)
    ↓
Logging (ELK Stack)
    ↓
Trace Analysis
```

---

## Decision 7: Testing Strategy

### Test Pyramid

```
        /\
       /  \  E2E Tests (1-5% of tests)
      /────\ Real LLM calls
     /______\
    /        \
   /  Integ.  \  Integration Tests (15-20%)
  /────────────\ Mock LLM
 /____________\
/              \
 Unit Tests     \  Unit Tests (75-80%)
/                \ Mocked service & AI
/__________________\
```

### Mock vs Real LLM Tests

| Test Type | Mock LLM | Real LLM |
|---|---|---|
| **Speed** | Fast (<100ms) | Slow (1-5s) |
| **Cost** | Free | $$ per test |
| **Reliability** | Deterministic | Can fail |
| **Frequency** | Every test run | Few times/week |
| **Purpose** | Development | Validation |

---

## Decision 8: Cost Optimization

### Cost Reduction Checklist

| Action | Potential Saving | Effort |
|---|---|---|
| **Use cheaper model** | 30-40% | Medium |
| **Cache responses** | 30-60% | Medium |
| **Batch requests** | 10-20% | High |
| **Token optimization** | 10-30% | Medium |
| **Load shedding** (skip AI under load) | Varies | Low |
| **Self-host (Ollama)** | 90%+ | High |

### Cost Example: 1M requests/month

```
Model: GPT-3.5 turbo
Avg tokens per request: 500 input, 100 output
Price: $0.005/1K input, $0.0015/1K output

Monthly cost WITHOUT optimization:
- 1M requests × 500 tokens × $0.005/1K = $2,500
- 1M requests × 100 tokens × $0.0015/1K = $150
- Total: ~$2,650/month

Monthly cost WITH 50% caching:
- 500K × ($2,500 + $150) = ~$1,325/month
- Savings: $1,325/month (50%)
```

---

## Your Decision: Start Here

**If you're building an AI feature:**

1. **Where?** → Service Layer
2. **Which LLM?** → Start with mock, then OpenAI/Claude
3. **Cache?** → Implement if repeat queries expected
4. **Sync or Async?** → Sync for now, async if latency critical
5. **Errors?** → Always fallback to traditional
6. **Monitor?** → Latency, cost, errors
7. **Test?** → Mock for unit tests, real LLM for feature validation
8. **Cost?** → Track and optimize caching first

---

Next: [Use Cases](../use-cases/product-search.md) →

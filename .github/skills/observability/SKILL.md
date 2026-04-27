# Observability Skill

## Purpose
Building comprehensive monitoring, logging, and tracing for AI systems.

## Four Pillars

### 1. Metrics (The Numbers)
Quantifiable measurements:
- Latency: P50, P95, P99
- Cost: $ per request, total spend
- Quality: Accuracy, relevance
- Availability: % of successful calls
- Cache hits: % served from cache

```java
metrics.timer("ai.request.latency")
    .tag("feature", "product-search")
    .tag("model", "gpt-3.5")
    .record(duration);
```

### 2. Logging (The Details)
Contextual debug information:
```json
{
  "timestamp": "2024-04-27T10:30:45Z",
  "traceId": "abc-123",
  "feature": "product-search",
  "event": "ai_call",
  "model": "gpt-3.5",
  "latencyMs": 234,
  "inputTokens": 350,
  "outputTokens": 45,
  "cost": 0.0009
}
```

### 3. Tracing (The Flow)
End-to-end request tracking:
```
User Request
  ↓ (10ms - database query)
Database Response
  ↓ (5ms - build context)
Context Ready
  ↓ (240ms - LLM call)
AI Response
  ↓ (5ms - parse & merge)
Final Response
```

### 4. Alerting (The Actions)
Automatic notifications:
```yaml
alerts:
  - name: HighLatency
    condition: p95_latency > 2000ms
    action: page_oncall
  
  - name: CostSpike
    condition: daily_cost > budget * 1.5
    action: notify_team
```

## Key Metrics by Domain

### Performance
- Latency: Track P50, P95, P99
- Throughput: req/sec measurement
- Error rate: % of failures

### Cost
- Per-request cost
- Per-feature cost
- Daily/monthly totals
- Cost trends (increasing?)

### Quality
- Parsing success rate
- Validation success rate
- User satisfaction rating
- Cache hit rate

### Business
- Feature adoption
- User impact (CTR, conversion)
- ROI calculation
- Cost per conversion

## Tools
- **Metrics**: Prometheus, Micrometer
- **Logging**: ELK stack, Datadog, Splunk
- **Tracing**: Jaeger, DataDog APM
- **Dashboards**: Grafana, DataDog

## When to Alert
```
Critical (page oncall):
├─ Error rate > 5%
├─ P95 latency > 5s
└─ AI service down

Warning (notify slack):
├─ Error rate > 2%
├─ Cache hit < 30%
└─ Cost trending up

Info (log only):
├─ New feature deployed
├─ Model switched
└─ Cache invalidated
```

---

**Next**: See `prompts/add-observability.md`

# Interview Preparation: System Design Questions

## Q1: Design an AI-enhanced e-commerce search system

### Requirements
- 1M requests/day
- <100ms p95 latency (user-facing)
- Budget: $5K/month for AI
- High accuracy in results

### Architecture
```
┌──────────────────────────────────────┐
│         API Gateway / Load Balancer  │
└─────────────┬──────────────────────┘
              │
       ┌──────▼──────┐
       │  Search     │
       │  Service    │
       └──────┬──────┘
              │
   ┌──────────┼──────────┐
   │          │          │
┌──▼──┐  ┌───▼──┐  ┌────▼────┐
│Cache│  │  DB  │  │AI Client │
└─────┘  └───┬──┘  └────┬────┘
        ┌────▼────────────┘
        │
  ┌─────▼─────┐
  │Multi-LLM  │
  │Load Bal   │
  └─────┬─────┘
        │
  ┌─────┴─────┬─────────┬──────────┐
  │OpenAI 60% │Claude   │Ollama 5% │
  │(quality)  │30% (alt)│(cost sav)│
  └───────────┴─────────┴──────────┘
```

### Key Decisions
1. **Caching Tier**: Redis for 60% cache hit
2. **Multi-Provider**: Load balance across 3 providers
3. **Async Only**: Background enrichment, immediate response
4. **Rate Limiting**: Queue bursts, process smoothly

---

## Q2: How would you scale to 100M requests/month?

### Cost Analysis
```
Current: 1M requests = $50/month (cached, cheap model)
Target: 100M requests = $5,000/month (without optimization)

Optimizations:
1. Semantic caching: 75% hit rate = $1,250/month
2. Batch processing: 20% more efficient = $1,000/month
3. Self-hosting: 15% moved to Ollama = $800/month
Target cost: ~$1,050/month ✅ (within $5K budget)
```

### Infrastructure
```
Service Replicas: 10
Cache Instances: 3 (Redis cluster)
LLM Connections: 100+ concurrent
Database: Sharded across regions
```

---

## Q3: Design for high accuracy recommendations

### Challenge
- Need personalization (context)
- Need ranking (which products best)
- Need explanation (why recommended)

### System Design
```
User Request
    ↓
Fetch User Profile
├─ Purchase history
├─ Browsing history
├─ Preferences
└─ Demographics
    ↓
Fetch Candidate Products
├─ Current inventory
├─ Similar items
└─ Trending items
    ↓
LLM Analysis
├─ Match user to products
├─ Rank by relevance
└─ Generate explanations
    ↓
Result Combination
├─ Validate output
├─ Filter by business rules
└─ Return to user
```

### Quality Metrics
- Click-through rate: >15%
- Purchase rate: >8%
- Return rate: <3%

---

Next: [Observability Questions](observability-qa.md)

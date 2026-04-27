# Comparing the Three Use Cases

## Overview Matrix

| Aspect | Product Search | Support Tickets | Recommendations |
|--------|---|---|---|
| **Frequency** | Per user query (1-5/user/month) | Per issue (5/user/year) | Often (multiple/session) |
| **Latency requirement** | <100ms p95 | <2s | <500ms |
| **Cost per call** | $0.001 | $0.001 | $0.001 |
| **Effective cost** | $0.0006 (40% cache) | $0.0001 (90% simple) | $0.0004 (60% cache) |
| **Complexity** | Ranking | Text generation | Ranking + explanation |
| **Quality impact** | Revenue +15% | Cost -90% | Revenue +15% |
| **Risk if AI fails** | Mediocre results | Escalated to human | Falls back to ML |
| **Best for** | E-commerce | Support | Any marketplace |

## Cost-Benefit Comparison

### Annual Impact (1M users, 10M queries served)

**Product Search**
```
Effective cost: 10M * $0.0006 = $6K
Incremental revenue (15% * $50 AOV): +$7.5M
ROI: 1,250,000x
```

**Support Tickets**
```
Small query volume but huge per-ticket savings
Effective cost: 100K tickets * $0.0001 = $10
Cost savings (92% reduction * $6.67/ticket): +$617K
ROI: 61,700x
```

**Recommendations**
```
Effective cost: 10M * $0.0004 = $4K
Incremental revenue (15% * $50 AOV): +$7.5M
ROI: 1,875,000x
```

## Implementation Complexity

```
Simplicity Ranking (simplest to hardest):

1. Support Tickets (★★)
   - Input: User's issue (text)
   - Output: Helpful response (text)
   - Validation: Easy (just check it's relevant)
   - Error handling: Simple (escalate if bad)

2. Product Search (★★★)
   - Input: Query + product catalog
   - Output: Ranked list
   - Validation: Must check accuracy
   - Error handling: Fallback ranking

3. Recommendations (★★★★)
   - Input: User profile + huge catalog
   - Output: Personalized ranked list
   - Validation: Must align with business rules
   - Error handling: Fallback to ML model
```

## When to Use Each

### Product Search
**Good for:**
- E-commerce sites
- Dense product catalogs (>10K items)
- Users searching with natural language
- Sites with high AOV

**Avoid:**
- Small catalogs (<1K items)
- Users click through lists anyway
- Keyword search is good enough

### Support Tickets  
**Good for:**
- High support volume
- Many FAQ-style questions
- Need immediate customer response
- Want to reduce support costs

**Avoid:**
- Complex support (needs specialized agent)
- Legal/compliance heavy
- Complaints that need empathy (LLM can't always deliver)

### Recommendations
**Good for:**
- Marketplaces (Amazon-like)
- Users with diverse preferences
- Want to increase AOV
- Have user history/profile

**Avoid:**
- Cold-start users (no profile)
- Simple stores (<100 products)
- Algorithmic ranking works well

## Decision Tree

```
Should I use AI for this feature?

    START
       ↓
   Does it involve
   ranking/ordering?
       │
       ├──Yes→ Use AI? (Search or Recommendations)
       └──No
           ↓
       Is it generating
       customer-facing text?
           │
           ├──Yes→ Use AI (Support Tickets)
           └──No
               ↓
           Probably don't use AI
           (simpler solution exists)
```

## Learning Path

**Start with Support Tickets:**
- Simplest to implement
- Easy to demonstrate value
- Quick wins build confidence

**Then Product Search:**
- More complex ranking
- Better ROI on e-commerce
- Good for interview discussion

**Then Recommendations:**
- Most complex
- Highest ROI
- Demonstrates full system design

---

## Interview Prompt

**"Design an AI system for a new use case of your choice."**

Suggested: Product Search (middle complexity)

Structure:
1. Explain the problem (traditional search is generic)
2. Show AI solution (LLM-based ranking)
3. Discuss architecture (service layer)
4. Analyze cost-benefit
5. Handle edge cases (cache, fallback, errors)
6. Monitor in production

---

# LLM Integration Overview

## What is an LLM?

A **Large Language Model (LLM)** is an AI system trained on vast amounts of text data that can:
- Understand context from prompts
- Generate human-like responses
- Perform reasoning and analysis
- Understand nuance and intent

### Common LLMs
- **OpenAI**: GPT-4, GPT-3.5 (API-based)
- **Anthropic**: Claude (API-based)
- **Open Source**: Llama, Mistral (self-hosted via Ollama)
- **Google**: PaLM, Gemini (API-based)

## The Traditional Spring Boot Flow

```
HTTP Request
    ↓
Controller (handles routing)
    ↓
Service (business logic)
    ↓
Repository (data access)
    ↓
Database/External APIs
    ↓
Response to Client
```

**Characteristics:**
- Deterministic results based on cached/stored data
- No AI reasoning or context understanding
- Fast and predictable
- Limited to pre-computed information

## The AI-Enhanced Flow

```
HTTP Request
    ↓
Controller (handles routing)
    ↓
Service Layer:
  1. Fetch data from DB
  2. Build context for LLM
  3. Call LLM with context
  4. Process LLM response
  5. Enhance original data
    ↓
Database/External APIs + LLM API
    ↓
Enhanced Response to Client
```

**Characteristics:**
- **Context-Aware**: Service provides rich context to LLM
- **Intelligent Processing**: LLM reasons about situation
- **Adaptive**: Different responses for different contexts
- **Slower**: Network latency to LLM
- **Costlier**: Pay per API call

## Why Integrate AI in the Service Layer?

### The Right Place
The **service layer** is ideal because:

1. **Already handles business logic** → Adding AI context is natural
2. **Has access to all data** → Can build rich prompts
3. **Can make decisions** → Decide when to call AI
4. **Encapsulates complexity** → Controllers don't care about AI
5. **Easy to test** → Mock AI client in unit tests

### NOT in the Controller
- ❌ Controllers should handle HTTP routing, not AI logic
- ❌ Couples HTTP handling with AI complexity
- ❌ Hard to test and maintain

### NOT as a Separate System
- ❌ Creates architectural complexity
- ❌ Requires new infrastructure/monitoring
- ❌ Hard to use AI results in existing features
- ❌ Increases latency (additional service calls)

## Key Pattern: Abstraction via Interface

```java
// This interface is key to flexibility
public interface AiClient {
    String generateResponse(String prompt);
    String generateResponseWithContext(String systemPrompt, String userPrompt);
    boolean isAvailable();
    String getModelName();
}
```

### Benefits of Abstraction

| Aspect | Benefit |
|--------|---------|
| **Provider Independence** | Switch OpenAI → Claude without changing services |
| **Testing** | Mock implementation for unit tests |
| **Gradual Rollout** | Start with mock, migrate to real LLM |
| **Fallback Logic** | `isAvailable()` lets you handle failures |
| **Cost Control** | Easier to swap to cheaper provider |

## Core AI Integration Concepts

### 1. Prompt Engineering
The quality of the LLM response depends heavily on the prompt:

```java
// Good: Provides context and clear instructions
String prompt = "You are a product recommendation assistant. " +
    "User's purchase history: " + userHistory + "\n" +
    "Available products: " + products + "\n" +
    "Recommend the top 5 products they would enjoy.";

// Bad: Vague and lacking context
String prompt = "Recommend products";
```

### 2. Context Building
Service enriches prompt with domain-specific data:

```java
// In ProductSearchService
private String buildSearchContext(ProductSearchRequest request, List<Product> results) {
    StringBuilder context = new StringBuilder();
    context.append("User Search: ").append(request.getQuery()).append("\n");
    context.append("Found Products:\n");
    results.forEach(p -> 
        context.append("- ").append(p.getName()).append(" ($").append(p.getPrice()).append(")\n")
    );
    return context.toString();
}
```

### 3. Response Processing
Don't use LLM output directly; process it:

```java
// In service layer
String aiResponse = aiClient.generateResponse(context);

// Process and validate
List<Product> recommendations = parseAiResponse(aiResponse);
// Filter based on business rules
recommendations = filterByInventory(recommendations);
// Rank by relevance
recommendations = rankByRelevance(recommendations, request);
```

### 4. Error Handling
LLMs can fail or produce invalid output:

```java
if (!aiClient.isAvailable()) {
    // Fallback to traditional approach
    return getRecommendationsWithoutAI();
}

try {
    String response = aiClient.generateResponse(prompt);
    return parseResponse(response);
} catch (LlmException e) {
    // Log, monitor, fallback
    log.error("LLM call failed", e);
    return getFallbackResponse();
}
```

## Decision Matrix: When to Use AI

| Scenario | Use AI? | Why/Why Not |
|----------|---------|------------|
| **Product Search** | ✅ Yes | Understands intent, can reason about relevance |
| **Exact Record Lookup** | ❌ No | LLM slower than index lookup |
| **Support Auto-Reply** | ✅ Yes | Needs reasoning and empathy |
| **Data Validation** | ❌ No | Deterministic rules are faster |
| **Recommendations** | ✅ Yes | Must understand user preferences |
| **User Authentication** | ❌ No | Security critical, must be deterministic |
| **Category Classification** | ~ Maybe | Depends on complexity vs. latency budget |
| **Summarization** | ✅ Yes | LLMs excel at this |

## LLM Call Cost Analysis

```
Cost = (Tokens Used) × (Price per Token)
```

**Token Pricing Examples (as of 2024):**
- GPT-4: $0.03 per 1K input tokens, $0.06 per 1K output tokens
- Claude 3 Opus: $0.015 per 1K input, $0.075 per 1K output
- Open Source (Ollama): $0 (self-hosted)

**Cost Optimization:**
1. Cache responses for similar requests
2. Batch prompts when possible
3. Use cheaper models for simple tasks
4. Implement request timeouts (don't retry indefinitely)
5. Monitor token usage per feature

## Performance Implications

### Latency
```
Traditional Flow:    50-200ms (DB query + serialization)
AI-Enhanced Flow:    50ms (DB) + 1-5s (LLM) + 50ms (processing)
                    = ~1-5.5 seconds
```

**Mitigation:**
- Cache LLM responses
- Run LLM calls asynchronously
- Implement timeouts (degrade to non-AI response)
- Use faster LLM models for latency-critical features

### Throughput
Each LLM API call consumes resources:
- Rate limiting from provider
- Network bandwidth
- Cost per request

**Mitigation:**
- Queue requests during high load
- Implement circuit breakers
- Use fallback to traditional method under load

## Common Implementation Patterns

### Pattern 1: Optional Enhancement
```
Request comes in
├─ Fetch data from DB
├─ If AI enabled for this request
│  ├─ Call LLM with context
│  └─ Enhance results
└─ Return response (with or without AI)
```

**Use Case**: Product search, recommendations
**Benefit**: Backwards compatible, clients opt-in

### Pattern 2: AI-as-Filter
```
Request comes in
├─ Get ALL candidate items
├─ Call LLM to score/rank items
├─ Return top N items
└─ Return response
```

**Use Case**: Recommendations filtering thousands of items
**Benefit**: LLM doesn't need to generate, just score

### Pattern 3: Async AI Enhancement
```
Request comes in
├─ Fetch primary data
├─ Return response immediately
├─ In background: Call LLM
├─ Store enhanced result for next request
└─ Next user gets pre-enhanced data
```

**Use Case**: Summaries, descriptions, categorization
**Benefit**: Doesn't block response, spreads cost over time

## Key Architectural Considerations

### Should I Use AI?

!!! warning "Think Before Adding AI"
    AI isn't always the answer. Ask:
    
    1. **Does it need reasoning?** → Yes = AI might help
    2. **Is latency critical?** → Yes = Avoid AI or cache
    3. **Is accuracy critical?** → Yes = Maybe avoid AI or use ensemble
    4. **What's the cost?** → Document $/request
    5. **What's the fallback?** → Must have one
    6. **Can it be cached?** → Yes = Definitely use
    7. **Can I test it?** → If no, reconsider

### Requirements for AI Integration

- Abstraction interface (AiClient)
- Fallback mechanism (if AI unavailable)
- Error handling (parsing, validation)
- Monitoring (latency, costs, errors)
- Caching strategy (if applicable)
- Clear context building
- Request timeout

---

## Next: Learn the Service Layer Pattern →

[Service Layer AI Pattern](service-layer-ai.md)

# Error Handling Skill

## Purpose
Robust error handling ensuring AI failures never crash the system.

## Error Types & Strategies

### 1. Timeout (Most Common)
**Cause**: LLM service slow or unresponsive

```java
try {
    String response = aiClient.generateResponse(prompt)
        .orTimeout(500, TimeUnit.MILLISECONDS)
        .get();
} catch (TimeoutException e) {
    logger.warn("AI timeout, using fallback");
    return traditionalResponse();
}
```

### 2. Invalid Response
**Cause**: AI returned unparseable output

```java
try {
    parseJsonResponse(aiResponse);
} catch (JsonProcessingException e) {
    logger.error("Invalid JSON from AI", e);
    metrics.counter("ai.parse_error").increment();
    throw new InvalidResponseException("Bad AI output", e);
}
```

### 3. Network Error
**Cause**: Connection issues, DNS failures

```java
for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
    try {
        return callAi(prompt);
    } catch (IOException e) {
        if (attempt < MAX_RETRIES) {
            long backoff = (long) (100 * Math.pow(2, attempt-1));
            Thread.sleep(backoff);  // Exponential backoff
        } else {
            throw new ServiceUnavailableException(e);
        }
    }
}
```

### 4. Rate Limiting
**Cause**: Too many API calls

```java
if (!rateLimiter.tryAcquire(1, 5, TimeUnit.SECONDS)) {
    return traditionalResponse();
}
```

### 5. Provider Outage
**Cause**: LLM service down

```java
for (AiClient provider : providers) {
    try {
        return provider.generateResponse(prompt);
    } catch (Exception e) {
        logger.warn("Provider failed, trying next", e);
    }
}
// All providers failed
throw new AllProvidersFailedException();
```

## Error Handling Pyramid

```
       Chaos Testing (5%)
    Integration Tests (15%)
 Unit + Circuit Breaker (80%)
```

## Best Practices

### ✅ Do
- Log with context (user, feature, error type)
- Return sensible fallback
- Track error rates per type
- Alert on error spikes
- Retry with exponential backoff
- Set appropriate timeouts

### ❌ Don't
- Bubble up raw exceptions to users
- Retry synchronously (blocks user)
- Treat all errors the same
- Ignore error patterns
- Retry on validation errors

## Fallback Strategy

```
Try AI
  ↓ timeout/error
Try Cache
  ↓ miss
Try Traditional Method
  ↓ failure
Return Graceful Degradation
```

## Circuit Breaker Pattern

```java
CircuitBreakerConfig config = CircuitBreakerConfig.custom()
    .failureRateThreshold(50)        // Fail if >50% errors
    .slowCallRateThreshold(50)       // Fail if >50% slow
    .slowCallDurationThreshold(5000) // >5s = slow
    .minimumNumberOfCalls(10)        // Need 10 calls to measure
    .waitDurationInOpenState(60000)  // Wait 60s before retry
    .build();
```

---

**Next**: See `prompts/add-provider.md` for implementation

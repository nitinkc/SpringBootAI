# LLM Integration Skill

## Purpose
This skill covers adding new LLM providers, implementing multi-model support, and building complex AI workflows.

## Key Concepts

### 1. Provider Implementation Pattern
Every LLM provider follows the same interface:

```java
public interface AiClient {
    String generateResponse(String prompt);
    String generateResponseWithContext(String context, String prompt);
    boolean isAvailable();
    String getModelName();
}

// Implementations:
class OpenAiClient implements AiClient { }
class AnthropicClient implements AiClient { }
class OllamaClient implements AiClient { }
class AzureOpenAiClient implements AiClient { }
```

### 2. Multi-Provider Load Balancing
Use weighted distribution:
```java
@Service
public class LoadBalancedAiClient implements AiClient {
    // Route based on cost, quality, availability
    // OpenAI 60% (best quality)
    // Claude 30% (alternative)
    // Ollama 10% (cost savings)
}
```

### 3. Token Counting & Cost Tracking
Every provider has different pricing:
```java
public double calculateCost(String model, int inputTokens, int outputTokens) {
    return switch (model) {
        case "gpt-3.5" -> (input * 0.000005) + (output * 0.0000015);
        case "gpt-4" -> (input * 0.00003) + (output * 0.00006);
        case "claude-3" -> (input * 0.000003) + (output * 0.000015);
        default -> 0;
    };
}
```

### 4. Context Window Management
Different models have different limits:
- GPT-3.5: 4K context
- GPT-4: 8K or 128K
- Claude 3: 100K context
- Ollama: Variable

```java
String compressContext(String context, int maxTokens) {
    // Summarize if too long
    if (estimateTokens(context) > maxTokens) {
        return summarizeWithAi(context);
    }
    return context;
}
```

## Best Practices

### ✅ Do
- Abstract behind AiClient interface
- Implement circuit breaker per provider
- Track costs per provider
- Set appropriate timeouts per provider
- Use connection pooling
- Cache tokens/cost for debugging
- Log provider failures and fallbacks

### ❌ Don't
- Call provider APIs directly in services
- Hard-code provider configuration
- mix provider logic into domain code
- Skip error handling
- Ignore token usage
- Use same timeout for all models

## Selection Criteria

| Scenario | Provider | Why |
|----------|----------|-----|
| Best quality, cost no issue | GPT-4 | Most capable |
| High volume, cost conscious | GPT-3.5 | 30x cheaper |
| Sensitive data, zero latency | Ollama | Local, no API calls |
| EU/GDPR compliance | Claude via EU | Data residency |
| Multi-modal (vision) | GPT-4V | Image understanding |

## Testing Providers
Always test locally first:
```java
@Bean
@Primary
@Profile("test")
public AiClient mockAiClient() {
    return new MockAiClient();
}
```

## Performance Metrics per Provider
Track these metrics:
- **Latency**: P50, P95, P99
- **Token efficiency**: Avg tokens per request
- **Cost**: $ spent, $ per request
- **Availability**: Success rate, errors
- **Quality**: User satisfaction rating

## When to Add a Provider
- If cost savings > 20%
- If latency improvement > 30%
- If quality improvement > 15%
- If required for compliance
- If unlocks new capability (vision, audio)

---

**Next**: See `prompts/add-provider.md` for step-by-step instructions

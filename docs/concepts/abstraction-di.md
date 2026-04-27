# Abstraction & Dependency Injection

## The AiClient Interface: Key to Flexibility

### Why Abstraction Matters

The **AiClient interface** is the cornerstone of flexible AI integration:

```java
public interface AiClient {
    String generateResponse(String prompt);
    String generateResponseWithContext(String systemPrompt, String userPrompt);
    boolean isAvailable();
    String getModelName();
}
```

### The Problem It Solves

**Without abstraction (tightly coupled):**
```java
@Service
public class ProductSearchService {
    
    private final OpenAiClient openAiClient;  // ❌ Coupled to OpenAI
    
    public void search(String query) {
        // Can only use OpenAI
        openAiClient.callApi(query);
    }
}
```

**Problems:**
- Can't switch to Claude (Claude team calls code)
- Can't switch to Ollama (different API)
- Testing requires OpenAI APIs
- Hard to mock for unit tests

**With abstraction (loosely coupled):**
```java
@Service
public class ProductSearchService {
    
    private final AiClient aiClient;  // ✅ Depends on interface
    
    public void search(String query) {
        // Works with any implementation
        aiClient.generateResponse(query);
    }
}
```

**Benefits:**
- ✅ Can swap implementations via Spring DI
- ✅ Easy to mock for testing
- ✅ Provider-agnostic
- ✅ Supports multiple implementations

## Dependency Injection in Action

### Spring's Magic: Constructor Injection

```java
@Service
public class ProductSearchService {
    
    private final AiClient aiClient;
    
    // Spring injects via constructor
    public ProductSearchService(AiClient aiClient) {
        this.aiClient = aiClient;
    }
}
```

**How Spring works:**
1. You define `AiClient` interface
2. You create implementations (OpenAiClient, ClaudeClient, etc.)
3. Mark implementation with `@Component` or `@Service`
4. Spring automatically injects the right implementation

### Multiple Implementations

```java
// Interface
public interface AiClient {
    // methods...
}

// Implementation 1: Mock (for development/testing)
@Component
public class MockAiClient implements AiClient {
    // Simulates LLM responses
}

// Implementation 2: OpenAI (for production)
@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "openai")
public class OpenAiClient implements AiClient {
    // Calls OpenAI API
}

// Implementation 3: Claude (alternative)
@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "claude")
public class AnthropicClient implements AiClient {
    // Calls Claude API
}
```

### Configuration-Based Selection

**application.yml:**
```yaml
ai:
  provider: openai  # Switch to 'claude' or 'mock' without code changes
  openai:
    api-key: ${OPENAI_API_KEY}
  claude:
    api-key: ${CLAUDE_API_KEY}
  mock:
    response-delay: 100ms
```

**Spring Configuration:**
```java
@Configuration
public class AiClientConfiguration {
    
    @Bean
    @ConditionalOnProperty(name = "ai.provider", havingValue = "openai")
    public AiClient openAiClient(OpenAiProperties props) {
        return new OpenAiClient(props);
    }
    
    @Bean
    @ConditionalOnProperty(name = "ai.provider", havingValue = "claude")
    public AiClient claudeClient(ClaudeProperties props) {
        return new AnthropicClient(props);
    }
    
    @Bean
    @ConditionalOnProperty(name = "ai.provider", havingValue = "mock", matchIfMissing = true)
    public AiClient mockClient() {
        return new MockAiClient();
    }
}
```

**Now services don't care which implementation is used:**
```java
@Service
public class ProductSearchService {
    
    private final AiClient aiClient;  // Could be any implementation
    
    public void search(String query) {
        aiClient.generateResponse(query);  // Works the same way
    }
}
```

## Design Patterns & Principles

### Strategy Pattern
The AiClient interface implements the **Strategy Pattern**:

- **Context**: ProductSearchService (uses AI)
- **Strategy Interface**: AiClient
- **Concrete Strategies**: OpenAiClient, AnthropicClient, MockAiClient

**Benefits:**
- Select algorithm (AI provider) at runtime
- Encapsulate algorithms
- Make them interchangeable

### SOLID Principles Demonstrated

| Principle | How We Use It |
|-----------|---|
| **Single Responsibility** | AiClient has one job: generate responses |
| **Open/Closed** | Service is open to new AI providers, closed to modification |
| **Liskov Substitution** | Any AiClient implementation works in ProductSearchService |
| **Interface Segregation** | AiClient interface is small, focused (not bloated) |
| **Dependency Inversion** | Service depends on abstraction (AiClient), not concrete (OpenAiClient) |

## Provider Switching: How It Works

### Scenario: Switch from OpenAI to Claude

**Before DI (❌ Painful):**
```java
// Change every service that uses OpenAiClient
@Service
public class ProductSearchService {
    private final AnthropicClient claudeClient;  // Had to edit
    
    public void search() {
        claudeClient.callClaude();  // Had to change method calls
    }
}

@Service
public class SupportService {
    private final AnthropicClient claudeClient;  // Had to edit here too
    
    public void handleTicket() {
        claudeClient.callClaude();  // Had to change method calls
    }
}

// Repeat in 20 more services...
```

**After DI (✅ Simple):**
```java
// No code changes needed!
// Just change application.yml:
ai:
  provider: claude  # Changed from 'openai'

// Spring automatically:
// 1. Injects AnthropicClient instead of OpenAiClient
// 2. All services automatically use Claude
// 3. No code modifications
```

## Best Practices

### 1. Always Program to Interfaces

```java
// ❌ Bad: Depends on concrete class
public class ProductSearchService {
    private final OpenAiClient client;  // Couples to OpenAI
}

// ✅ Good: Depends on interface
public class ProductSearchService {
    private final AiClient client;  // Works with any provider
}
```

### 2. Constructor Injection Over Field Injection

```java
// ❌ Anti-pattern: Field injection
@Service
public class ProductSearchService {
    @Autowired
    private AiClient aiClient;  // Hard to test, unclear dependencies
}

// ✅ Good: Constructor injection
@Service
public class ProductSearchService {
    private final AiClient aiClient;
    
    public ProductSearchService(AiClient aiClient) {
        this.aiClient = aiClient;  // Clear dependency, easy to test
    }
}
```

### 3. Mock for Testing

```java
// Unit test with mock
@SpringBootTest
public class ProductSearchServiceTest {
    
    @MockBean
    private AiClient aiClient;
    
    @Autowired
    private ProductSearchService service;
    
    @Test
    void testSearch() {
        // Setup mock behavior
        when(aiClient.generateResponse(anyString()))
            .thenReturn("Mock response");
        
        // Test service
        ProductSearchResponse response = service.searchProducts(...);
        
        // Assert
        assertNotNull(response);
    }
}
```

### 4. Make Implementations Pluggable

```java
// Create factory-like setup
@Configuration
public class AiConfiguration {
    
    @Bean
    public AiClient aiClient(
        @Value("${ai.provider:mock}") String provider,
        OpenAiConfig openAiConfig,
        ClaudeConfig claudeConfig) {
        
        return switch (provider) {
            case "openai" -> new OpenAiClient(openAiConfig);
            case "claude" -> new AnthropicClient(claudeConfig);
            default -> new MockAiClient();
        };
    }
}
```

## Common Mistakes

### Mistake 1: Concrete Injection
```java
// ❌ Don't do this
@Autowired
private OpenAiClient openAiClient;  // Couples to OpenAI

// ✅ Do this
@Autowired
private AiClient aiClient;  // Decoupled from provider
```

### Mistake 2: Service Depends on Another Service's AI
```java
// ❌ Bad: Creates hidden dependency
@Service
public class RecommendationService {
    @Autowired
    private ProductSearchService productService;  // Uses product search's AI
}

// ✅ Good: Each service has own AI dependency
@Service
public class RecommendationService {
    @Autowired
    private AiClient aiClient;  // Independent AI client
}
```

### Mistake 3: Mixing Implementations
```java
// ❌ Bad: Services hardcoded to different providers
public class ProductService {
    private final OpenAiClient ai;
}

public class RecommendationService {
    private final AnthropicClient ai;  // Different provider!
}

// ✅ Good: One provider interface, different implementations selected by config
public class ProductService {
    private final AiClient ai;  // Selected by config
}

public class RecommendationService {
    private final AiClient ai;  // Same selection mechanism
}
```

## Lifecycle: Interface → Implementation → Provider

```
1. Define Interface
   └─ AiClient with contract

2. Create Implementations
   ├─ MockAiClient (for dev)
   ├─ OpenAiClient (for production)
   └─ AnthropicClient (for alternative)

3. Mark with @Component/@Service
   └─ Spring knows about implementations

4. Use @Conditional* to Select
   └─ Spring picks right implementation

5. Inject into Services
   └─ Services get whatever implementation is selected

6. Switch Implementation
   └─ Just change config, no code changes
```

---

Next: [Design Decision Matrix](design-decisions.md) →

# Interview Preparation: Design Pattern Questions

## Q1: What design patterns are you using?

### Answer: Strategy Pattern

**Pattern:** Strategy (Behavioral)
**Purpose:** Select algorithm (AI provider) at runtime

```
Context: ProductSearchService
├─ Strategy Interface: AiClient
└─ Concrete Strategies:
   ├─ MockAiClient
   ├─ OpenAiClient
   ├─ AnthropicClient
   └─ OllamaClient
```

**Benefits:**
- Select provider without changing service code
- Easy to test (mock implementation)
- Encapsulates algorithm differences
- Makes providers interchangeable

## Q2: Why abstraction via interface?

### Answer: Interface Segregation & Dependency Inversion

```java
// ✅ Depend on abstraction
private final AiClient aiClient;

// ❌ Don't depend on concrete class
private final OpenAiClient openAiClient;
```

**SOLID Principles:**
- **I (Interface Segregation)**: AiClient is minimal, focused
- **D (Dependency Inversion)**: Service depends on interface, not implementation

## Q3: How do you test with AI dependency?

### Answer: Dependency Injection + Mocking

```java
@SpringBootTest
class ProductSearchServiceTest {
    
    @MockBean
    private AiClient aiClient;
    
    @Autowired
    private ProductSearchService service;
    
    @Test
    void testAIEnhancement() {
        // Mock unexpected as needed
        when(aiClient.generateResponse(anyString()))
            .thenReturn("Mock response");
        
        // Test without hitting real API
        ProductSearchResponse result = service.search(request);
        
        // Verify behavior
        verify(aiClient).generateResponse(any());
    }
}
```

## Q4: What's the difference between Service Pattern and your approach?

### Answer

**Service Locator Pattern** (❌ Anti-pattern):
```java
@Service
public class ProductSearchService {
    private final ServiceLocator serviceLocator;
    
    public void search() {
        // Look up dependency at runtime
        AiClient ai = serviceLocator.getService(AiClient.class);
        ai.generate(prompt);
    }
}
```

**Dependency Injection** (✅ Our approach):
```java
@Service
public class ProductSearchService {
    private final AiClient aiClient;  // Provided by Spring
    
    public void search() {
        aiClient.generate(prompt);
    }
}
```

**Why DI is better:**
- Clear dependencies (visible in constructor)
- Easier to test
- Spring manages lifecycle
- No"magic" lookups

## Q5: Explain Repository Pattern usage

### Answer

```
Service
   ↓ (depends on)
Repository Interface
   ↓ (implemented by)
JPA Repository
   ↓ (accesses)
Database
```

**Benefits:**
- Service doesn't know about database
- Easy to swap implementations
- Testable (mock repository)
- Central place for data access logic

## Q6: How does Dependency Injection work in Spring?

### Answer

**3 Ways to Inject Dependencies:**

1. **Constructor Injection** (✅ Best)
```java
public ProductSearchService(AiClient aiClient) {
    this.aiClient = aiClient;  // Clear, testable
}
```

2. **Setter Injection** (⚠️ Okay)
```java
@Autowired
public void setAiClient(AiClient client) {
    this.aiClient = client;  // Optional dependency?
}
```

3. **Field Injection** (❌ Avoid)
```java
@Autowired
private AiClient aiClient;  // Hidden dependency, hard to test
```

**Why Constructor is Best:**
- All dependencies visible
- Can make final (immutable)
- Easy to construct in tests
- Clearly required dependencies

## Q7: Explain the Factory Pattern

### Answer

**Our Implementation:**
```java
@Configuration
public class AiClientFactory {
    
    @Bean
    @ConditionalOnProperty(name = "ai.provider", havingValue = "openai")
    public AiClient openAiClient(OpenAiConfig config) {
        return new OpenAiClient(config);
    }
    
    @Bean
    @ConditionalOnProperty(name = "ai.provider", havingValue = "claude")
    public AiClient claudeClient(ClaudeConfig config) {
        return new AnthropicClient(config);
    }
}
```

**Purpose:**
- Create appropriate implementation based on config
- Spring doesn't need to know concrete classes
- Easy to add new providers

## Q8: Real-world analogy for Service Layer AI?

### Answer

**Restaurant Analogy:**

```
Traditional Restaurant:
Customer → Server → Cook → Ingredients → Dish
(Service reads menu, cooks prepare)

AI-Enhanced Restaurant:
Customer → Server → Consulting Chef → Look at ingredients
         (analyzes request deeply, suggests best dish)
         → Cook → Ingredients → Better Dish
```

**In Code:**
```
Traditional:
Request → Controller → Service → DB → Response

AI-Enhanced:
Request → Controller → Service:
           ├─ Get DB data
           ├─ Analyze with AI  (consulting chef)
           └─ Return enhanced response
        → Response
```

---

Next: [System Design Questions](system-design.md)

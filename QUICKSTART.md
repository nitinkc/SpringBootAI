# 🚀 Quick Start Guide

Get the AI-powered Spring Boot demo running in 3 minutes!

## Prerequisites
- Java 17+ installed
- Maven 3.8+ installed

## Start the Application

```bash
# Navigate to project directory
cd /Users/sgovinda/Learn/springbootAI

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

You should see:
```
✓ Spring Boot starts
✓ H2 database initializes
✓ Sample data loads (10 products, 3 users)
✓ Server runs on http://localhost:8080/api
```

## Test the APIs

### In Terminal 1: Keep app running
```bash
mvn spring-boot:run
```

### In Terminal 2: Test the endpoints

```bash
# 1. Search Products WITHOUT AI
curl "http://localhost:8080/api/products/search?query=laptop&useAIEnhancement=false"

# 2. Search Products WITH AI Enhancement
curl "http://localhost:8080/api/products/search?query=laptop&useAIEnhancement=true"

# 3. Create Support Ticket (gets AI auto-response)
curl -X POST "http://localhost:8080/api/support/tickets" \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Doe",
    "email": "john@example.com",
    "issue": "I cannot login to my account"
  }'

# 4. Get Personalized Recommendations
curl "http://localhost:8080/api/recommendations?userId=1&limit=5"
```

Or use the test script:
```bash
bash test-api.sh
```

## Understanding the Demo

### Traditional Flow
```
Client → Controller → Service → Database → Response
```

### AI-Enhanced Flow (This Demo)
```
Client → Controller → Service (+ AiClient) → 
         Fetch Data → Build Context → Call LLM → Enhance Response
```

### Three Use Cases:

**1. Product Search**
- Without AI: Regular database search
- With AI: Database results + AI recommendations

**2. Customer Support**
- Submit issue → Automatically generate helpful response
- Shows instant customer support without human intervention

**3. Recommendations**
- Get user profile + products → AI analyzes → Personalized picks
- Shows how AI can personalize services

## Key Files to Review

- [README.md](README.md) - Full documentation
- [ProductSearchService.java](src/main/java/com/example/aispring/service/ProductSearchService.java) - Example of AI integration
- [AiClient.java](src/main/java/com/example/aispring/client/AiClient.java) - AI abstraction interface
- [MockAiClient.java](src/main/java/com/example/aispring/client/MockAiClient.java) - Mock LLM implementation

## Next Steps

### Swap the Mock AI with Real LLM
Edit [AiClient.java](src/main/java/com/example/aispring/client/AiClient.java) and create:

**Option A: OpenAI (GPT-4)**
```java
@Component
public class OpenAiClient implements AiClient { 
    // Call OpenAI API
}
```

**Option B: Anthropic Claude**
```java
@Component
public class AnthropicClient implements AiClient { 
    // Call Claude API
}
```

**Option C: Local Ollama**
```java
@Component
public class OllamaClient implements AiClient { 
    // Call local Ollama server
}
```

**No service code changes needed** - Spring's dependency injection handles swapping!

## Troubleshooting

**Port 8080 already in use?**
```bash
# Edit src/main/resources/application.yml
server:
  port: 8081  # Change to another port
```

**Build fails?**
```bash
# Clean everything and rebuild
mvn clean install -DskipTests
```

**Need more data?**
Edit [DataInitializationConfig.java](src/main/java/com/example/aispring/config/DataInitializationConfig.java) to add more sample products/users.

## 📚 Learn More
- See [README.md](README.md) for comprehensive documentation
- Each service has detailed comments explaining the flow
- Controllers show REST endpoint patterns

---

**Key Concept**: AI integration doesn't require system redesign - it's just another service layer enhancement! 🧠

# 🧠 Spring Boot AI Integration Demo

A practical demonstration of how to integrate **AI/LLM capabilities** into Spring Boot services **without redesigning the entire system**.

## 📊 Architecture Concept

### Traditional Flow
```
Client → Controller → Service → DB/APIs → Response
```

### AI-Enhanced Flow (This Project)
```
Client → Controller → Service (AI Orchestrator)
                    → Fetch Data
                    → Build Context
                    → Call LLM
                    → Return Smarter Response
```

## 💡 Key Takeaway

**AI is added INSIDE the service layer, NOT as a separate system.**

This means:
- ✅ Your existing controllers stay the same
- ✅ Your data layer remains unchanged
- ✅ AI enhancement is optional per request
- ✅ Easy to swap LLM providers (OpenAI, Claude, Ollama, etc.)

## 🎯 Three Use Cases Demonstrated

### 1. **Product Search** (AI-Enhanced Search Results)
- Traditional: Search DB → Return raw results
- AI-Enhanced: Search DB → Build context → Call LLM → Smart recommendations

**Flow:** Client → ProductSearchController → ProductSearchService (+ AiClient) → ProductRepository

### 2. **Customer Support** (AI Auto-Replies)
- Traditional: Create ticket → Save → Return confirmation
- AI-Enhanced: Create ticket → Call LLM → Generate response → Save response

**Flow:** Client → SupportController → SupportService (+ AiClient) → SupportTicketRepository

### 3. **Personalized Recommendations** (AI-Powered Suggestions)
- Traditional: Get user → Show generic products
- AI-Enhanced: Get user → Analyze preferences → Call LLM → Personalized picks

**Flow:** Client → RecommendationController → RecommendationService (+ AiClient) → Repositories

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.8+

### Installation

```bash
# Clone/navigate to project
cd springbootAI

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📡 API Endpoints

### 1. Product Search API

#### GET - Simple Search
```bash
curl "http://localhost:8080/api/products/search?query=laptop&useAIEnhancement=false"
```

#### GET - AI-Enhanced Search
```bash
curl "http://localhost:8080/api/products/search?query=laptop&useAIEnhancement=true"
```

#### GET - Category Filter with AI
```bash
curl "http://localhost:8080/api/products/search?category=electronics&maxPrice=1000&useAIEnhancement=true"
```

#### POST - Complex Search
```bash
curl -X POST "http://localhost:8080/api/products/search" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "high performance laptop",
    "category": "electronics",
    "maxPrice": 2000,
    "useAIEnhancement": true
  }'
```

### 2. Support Tickets API

#### POST - Create Ticket with AI Response
```bash
curl -X POST "http://localhost:8080/api/support/tickets" \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Doe",
    "email": "john@example.com",
    "issue": "I cannot login to my account, getting access denied error"
  }'
```

#### GET - Retrieve Ticket
```bash
curl "http://localhost:8080/api/support/tickets/1"
```

### 3. Recommendations API

#### GET - Get Recommendations
```bash
curl "http://localhost:8080/api/recommendations?userId=1&limit=5"
```

#### GET - Category-Specific Recommendations
```bash
curl "http://localhost:8080/api/recommendations?userId=2&category=electronics&limit=3"
```

#### POST - Get Recommendations
```bash
curl -X POST "http://localhost:8080/api/recommendations" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "limit": 5,
    "category": "electronics"
  }'
```

## 🔧 Project Structure

```
src/main/java/com/example/aispring/
├── AiSpringApplication.java          # Main Spring Boot app
├── controller/
│   ├── ProductSearchController.java   # REST endpoints for search
│   ├── SupportController.java         # REST endpoints for support
│   └── RecommendationController.java  # REST endpoints for recommendations
├── service/
│   ├── ProductSearchService.java      # Search logic + AI integration
│   ├── SupportService.java            # Support logic + AI integration
│   └── RecommendationService.java     # Recommendation logic + AI integration
├── client/
│   ├── AiClient.java                  # AI interface (abstraction)
│   └── MockAiClient.java              # Mock implementation
├── model/
│   ├── Product.java                   # Product entity
│   ├── SupportTicket.java             # Support ticket entity
│   └── User.java                      # User entity
├── dto/
│   ├── ProductSearchRequest.java
│   ├── ProductSearchResponse.java
│   ├── SupportTicketRequest.java
│   ├── SupportTicketResponse.java
│   ├── RecommendationRequest.java
│   └── RecommendationResponse.java
├── repository/
│   ├── ProductRepository.java         # Data access layer
│   ├── SupportTicketRepository.java
│   └── UserRepository.java
└── config/
    └── DataInitializationConfig.java  # Test data setup
```

## 🧠 How AI Integration Works

### The AiClient Interface

```java
public interface AiClient {
    String generateResponse(String prompt);
    String generateResponseWithContext(String systemPrompt, String userPrompt);
    boolean isAvailable();
    String getModelName();
}
```

**Why this matters:** You can swap implementations without changing services!

### Service Layer Integration

Example from `ProductSearchService`:

```java
// Step 1: Fetch data from DB
List<Product> results = searchDatabase(request);

// Step 2: Build context
String context = buildSearchContext(request, results);

// Step 3: Call LLM
String aiResponse = aiClient.generateResponseWithContext(
    "You are a product recommendation assistant...",
    context + "\nWhat products do you recommend?"
);

// Step 4: Return enhanced response
response.setAiSuggestion(aiResponse);
```

## 🔄 Swapping LLM Providers

The beauty of this architecture is **you only need to implement the `AiClient` interface**:

### OpenAI Implementation Example
```java
@Component
public class OpenAiClient implements AiClient {
    // Call OpenAI API
    @Override
    public String generateResponse(String prompt) {
        // Use openai-java library
        return openaiService.createCompletion(prompt).getChoices(0).getText();
    }
}
```

### Anthropic Claude Implementation Example
```java
@Component
public class AnthropicClient implements AiClient {
    @Override
    public String generateResponse(String prompt) {
        // Use anthropic-sdk-java
        return anthropicClient.sendMessage(prompt);
    }
}
```

### Local Ollama Implementation Example
```java
@Component
public class OllamaClient implements AiClient {
    @Override
    public String generateResponse(String prompt) {
        // Call local Ollama server
        return ollamaService.generate("llama2", prompt);
    }
}
```

**No changes needed in services!** Just implement `AiClient` and Spring Boot's dependency injection handles the rest.

## 📈 Sample Response

### Product Search with AI
```json
{
  "results": [
    {
      "id": 1,
      "name": "Premium Laptop - 15\" Display",
      "description": "High-performance laptop with RTX 4080...",
      "price": 1899.99,
      "category": "Electronics",
      "stockQuantity": 15,
      "relevanceScore": "HIGH"
    }
  ],
  "aiSuggestion": "Based on your search for 'laptop', I recommend the Premium Laptop 15\". It features an RTX 4080 which is excellent for programming and video editing...",
  "enhancedWithAI": true,
  "reasoningExplanation": "Results enhanced with AI analysis of your query and available products"
}
```

### Support Ticket with AI Response
```json
{
  "ticketId": 1,
  "customerName": "John Doe",
  "status": "ASSIGNED",
  "issue": "I cannot login to my account",
  "aiResponse": "Thank you for contacting us. I understand you're having login issues. Here are the most common solutions: 1) Clear your browser cache and cookies... 2) Try a different browser... 3) Reset your password...",
  "aiModel": "MockAI-v1.0",
  "processingTimeMs": 145
}
```

## 🎓 Learning Points

1. **Abstraction is Key**: The `AiClient` interface allows you to swap LLM providers without touching service code
2. **Layered Architecture**: Keep concerns separated (controllers, services, data layer)
3. **Context Building**: LLMs perform better with rich context - this is done in the service layer
4. **Optional Enhancement**: Traditional flow still works; AI is opt-in
5. **No System Redesign Needed**: Adding AI shouldn't break existing functionality

## 🚀 Next Steps - Integration with Real LLMs

### To use OpenAI:
1. Add dependency: `com.openai:spring-boot-starter-openai`
2. Create `OpenAiClient` implementing `AiClient`
3. Add API key to `application.yml`
4. Spring automatically injects your implementation

### To use Claude:
1. Add Anthropic SDK dependency
2. Create `AnthropicClient` implementing `AiClient`
3. No service code changes needed!

### To use Local Ollama:
1. Install Ollama locally
2. Create `OllamaClient` implementing `AiClient`
3. Point to your local Ollama server

## 📝 Database

- **Engine**: H2 (in-memory for demo)
- **JPA**: Hibernate
- **Data**: Auto-loaded on startup with sample products and users

Access H2 console at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: (leave blank)

## 🔍 Key Files to Review

| File | Purpose |
|------|---------|
| [AiClient.java](src/main/java/com/example/aispring/client/AiClient.java) | LLM interface - the abstraction layer |
| [ProductSearchService.java](src/main/java/com/example/aispring/service/ProductSearchService.java) | Shows step-by-step AI integration |
| [MockAiClient.java](src/main/java/com/example/aispring/client/MockAiClient.java) | Example implementation |
| [application.yml](src/main/resources/application.yml) | Configuration |

## 🐛 Troubleshooting

### Application won't start
```bash
# Check Java version
java -version  # Should be 17+

# Check Maven installation
mvn -v

# Clean rebuild
mvn clean install
```

### Port 8080 already in use
Edit `src/main/resources/application.yml`:
```yaml
server:
  port: 8081  # Change to different port
```

## 📚 Theory Behind the Architecture

This project demonstrates the **Single Responsibility Principle**:
- **Controllers**: Handle HTTP requests/responses
- **Services**: Contain business logic and AI orchestration
- **Repositories**: Handle data persistence
- **AiClient**: Encapsulates LLM communication

Each layer has ONE responsibility, making the code:
- ✅ Easy to test
- ✅ Easy to modify
- ✅ Easy to scale
- ✅ Easy to swap components (like LLM providers)

## 📞 Support

For questions or issues, review:
1. Application logs (DEBUG level enabled)
2. The code comments in service classes
3. REST API examples above

---

**Remember:** AI is just another tool in your service layer. Treat it like any other external dependency - with proper abstraction and dependency injection! 🚀

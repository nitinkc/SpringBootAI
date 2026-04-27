# 🧠 Spring Boot AI Integration Demo

A practical demonstration of how to integrate **AI/LLM capabilities** into Spring Boot services **without redesigning the entire system**.

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.8+

### Run the Application
```bash
cd /Users/sgovinda/Learn/springbootAI
mvn clean install
mvn spring-boot:run
```

The application will:
- ✅ Start on `http://localhost:8080`
- ✅ Automatically create H2 in-memory database
- ✅ Load 10 sample products and 3 users
- ✅ Expose REST APIs at `/api/*`

### Test the APIs
```bash
# Product search with AI enhancement
curl "http://localhost:8080/api/products/search?query=laptop&useAIEnhancement=true"

# Create support ticket (gets AI auto-response)
curl -X POST "http://localhost:8080/api/support/tickets" \
  -H "Content-Type: application/json" \
  -d '{"customerName": "John Doe", "email": "john@example.com", "issue": "I cannot login"}'

# Get personalized recommendations
curl "http://localhost:8080/api/recommendations?userId=1&limit=5"
```

Or use the test script:
```bash
bash test-api.sh
```

---

## 📚 Complete Documentation via MkDocs

This project includes **comprehensive documentation**. View it locally:

### Setup Documentation Environment

#### Option 1: Complete Setup with Virtual Environment (Recommended)
```bash
# Step 1: Create a Python virtual environment
python3 -m venv .venv

# Step 2: Activate the virtual environment
# On macOS/Linux:
source .venv/bin/activate
# On Windows:
# .venv\Scripts\activate

# Step 3: Install dependencies from requirements.txt
pip install -r requirements.txt

# Step 4: Serve documentation (auto-reloads on file changes)
mkdocs serve

# Step 5: Open in your browser
# Visit: http://localhost:8000

# To deactivate virtual environment when done:
deactivate
```

#### Option 2: Minimal Setup (if you have dependency issues)
```bash
# Create and activate virtual environment
python3 -m venv .venv
source .venv/bin/activate

# Install minimal dependencies (no optional packages)
pip install -r requirements-minimal.txt

# Serve documentation
mkdocs serve

# Open: http://localhost:8000
```

#### Option 3: Quick Setup (without virtual environment)
```bash
# Install dependencies directly
pip install -r requirements.txt

# Serve documentation
mkdocs serve

# Open: http://localhost:8000
```

#### Option 4: Manual Installation
```bash
# Create and activate virtual environment
python3 -m venv venv
source venv/bin/activate  # macOS/Linux

# Install individual packages
pip install mkdocs==1.5.3 mkdocs-material==9.5.3 pymdown-extensions==10.5

# Serve documentation
mkdocs serve
```

### View Documentation
Once documentation is serving, open **http://localhost:8000** in your browser.

The documentation site will:
- ✅ Auto-reload when you edit files in `docs/`
- ✅ Show live preview of changes
- ✅ Include search functionality
- ✅ Display 24 comprehensive documentation pages

### 📖 Documentation Sections

#### **Core Concepts** (Understand the Architecture)
- [LLM Integration Overview](docs/concepts/llm-integration.md) - Why integrate AI?
- [Service Layer AI Pattern](docs/concepts/service-layer-ai.md) - Architecture pattern
- [Abstraction & Dependency Injection](docs/concepts/abstraction-di.md) - Why abstractions matter
- [Design Decision Matrix](docs/concepts/design-decisions.md) - Trade-offs explained

#### **Three Use Cases** (Practical Implementations)
- [Product Search](docs/use-cases/product-search.md) - AI-enhanced search ranking
- [Support Tickets](docs/use-cases/support-tickets.md) - Auto-response generation
- [Recommendations](docs/use-cases/recommendations.md) - Personalized suggestions
- [Pattern Comparison](docs/use-cases/pattern-comparison.md) - When to use each pattern

#### **Observability** (Monitor & Optimize)
- [Monitoring & Metrics](docs/observability/monitoring.md) - What to measure
- [Logging Strategy](docs/observability/logging.md) - Structured logging
- [Cost Tracking](docs/observability/cost-tracking.md) - Monitor spending
- [Performance Analysis](docs/observability/performance.md) - Latency & SLAs

#### **Implementation** (Production Patterns)
- [Best Practices](docs/implementation/best-practices.md) - Proven patterns
- [Performance Optimization](docs/implementation/performance.md) - Caching strategies
- [Error Handling](docs/implementation/error-handling.md) - Resilience patterns

#### **Interview Preparation** (25+ Questions)
- [Architecture Q&A](docs/interview/architecture-qa.md) - System design questions
- [Design Pattern Q&A](docs/interview/design-patterns.md) - SOLID principles
- [System Design Q&A](docs/interview/system-design.md) - Scale scenarios
- [Observability Q&A](docs/interview/observability-qa.md) - Monitoring questions
- [Behavioral Q&A](docs/interview/behavioral.md) - Soft skills

#### **Future Development** (Extend the Project)
- [Skills, Prompts & Agents](docs/future/skills-prompts-agents.md) - How to extend
- [Available Agents](docs/future/agents.md) - AI automation helpers
- [Future Use Cases](docs/future/use-cases.md) - 8 ready-to-implement features

---

## 💡 Architecture Overview

### Traditional Flow
```
Client → Controller → Service → Database → Response
```

### AI-Enhanced Flow (This Project)
```
Client → Controller → Service (AI Orchestrator)
                   ├─ Fetch Data
                   ├─ Build Context
                   ├─ Call LLM
                   └─ Enhance Response
```

**Key Insight:** AI is integrated INSIDE the service layer, not as a separate system!

---

## 🎯 Three Production-Ready Use Cases

### 1. Product Search
**What it does:** Uses AI to intelligently rank products based on user intent

**API Endpoint:**
```bash
POST /api/products/search
{
  "query": "high performance laptop",
  "category": "electronics",
  "maxPrice": 2000,
  "useAIEnhancement": true
}
```

### 2. Customer Support
**What it does:** Generates immediate AI-powered responses to support tickets

**API Endpoint:**
```bash
POST /api/support/tickets
{
  "customerName": "John Doe",
  "email": "john@example.com",
  "issue": "I cannot login to my account"
}
```

### 3. Personalized Recommendations
**What it does:** Analyzes user profile and suggests personalized products

**API Endpoint:**
```bash
GET /api/recommendations?userId=1&limit=5&category=electronics
```

---

## 🏗️ Project Structure

```
src/main/java/com/example/aispring/
├── AiSpringApplication.java              # Main application
├── client/
│   ├── AiClient.java                     # LLM interface (abstraction)
│   └── MockAiClient.java                 # Mock implementation
├── controller/
│   ├── ProductSearchController.java       # REST endpoints
│   ├── SupportController.java
│   └── RecommendationController.java
├── service/
│   ├── ProductSearchService.java          # AI orchestration
│   ├── SupportService.java
│   └── RecommendationService.java
├── model/
│   ├── Product.java                      # JPA entities
│   ├── SupportTicket.java
│   └── User.java
├── dto/
│   ├── ProductSearchRequest.java          # Request/response DTOs
│   ├── ProductSearchResponse.java
│   └── [5 more DTOs]
├── repository/
│   ├── ProductRepository.java             # Data access layer
│   ├── SupportTicketRepository.java
│   └── UserRepository.java
└── config/
    └── DataInitializationConfig.java      # Auto-load sample data
```

---

## 🔄 Swapping LLM Providers

The beauty of this architecture is **you only need to implement the `AiClient` interface**:

### OpenAI Example
```java
@Component
public class OpenAiClient implements AiClient {
    @Override
    public String generateResponse(String prompt) {
        // Call OpenAI API
        return openaiService.createCompletion(prompt).getText();
    }
}
```

### Anthropic Claude Example
```java
@Component
public class AnthropicClient implements AiClient {
    @Override
    public String generateResponse(String prompt) {
        return anthropicClient.sendMessage(prompt);
    }
}
```

### Local Ollama Example
```java
@Component
public class OllamaClient implements AiClient {
    @Override
    public String generateResponse(String prompt) {
        return ollamaService.generate("llama2", prompt);
    }
}
```

**No service code changes needed!** Spring's dependency injection handles provider swapping automatically.

---

## 🧠 Key Learning Points

1. **Abstraction is Key**: The `AiClient` interface allows swapping LLM providers without touching service code
2. **Service Layer Pattern**: AI enhancement happens in services, not controllers
3. **Context Building**: LLMs perform better with rich context - handled in service layer
4. **Optional Enhancement**: Traditional flow still works; AI is opt-in
5. **No Redesign Required**: Adding AI doesn't break existing functionality

---

## 🔧 Extending the Project

Want to add new features? Follow these steps:

### Step 1: Read Skills
Learn best practices for your domain:
```
.github/skills/llm-integration/SKILL.md
.github/skills/performance-optimization/SKILL.md
.github/skills/testing-strategy/SKILL.md
```

### Step 2: Read Prompts
Get step-by-step instructions:
```
prompts/add-provider.md
prompts/add-use-case.md
prompts/optimize-performance.md
```

### Step 3: Use Agents (Optional)
Let AI help with implementation:
```
@llm-integration "Add OpenAI provider"
@performance-tuning "Optimize latency"
@test-generation "Create tests for review sentiment"
```

**Full guide:** See [Skills, Prompts & Agents](docs/future/skills-prompts-agents.md)

---

## 📈 Future Use Cases

8 production-ready features ready to implement:

**Easy (1 day)**
- Review Sentiment Analysis
- Product Auto-tagging

**Medium (2-3 days)**
- Review Summarization
- Content Moderation
- Dynamic Pricing

**Hard (4-5 days)**
- Fraud Detection
- Demand Forecasting
- QA Chatbot

See [Future Use Cases](docs/future/use-cases.md) for details on each.

---

## 🗄️ Database

- **Engine**: H2 (in-memory for demo)
- **ORM**: Hibernate + Spring Data JPA
- **Data**: Auto-loaded on startup

Access H2 console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: (leave blank)

---

## 📞 Troubleshooting

### Application won't start
```bash
# Check Java version (needs 17+)
java -version

# Clean rebuild
mvn clean install -DskipTests
```

### Port 8080 already in use
Edit `src/main/resources/application.yml`:
```yaml
server:
  port: 8081
```

### Build fails with dependencies
```bash
mvn clean install
mvn spring-boot:run
```

---

## 📚 Interview Preparation

This project is designed for technical interview preparation:

### 1. Understanding Phase (1 hour)
Read:
- [LLM Integration Overview](docs/concepts/llm-integration.md)
- [Service Layer AI Pattern](docs/concepts/service-layer-ai.md)

### 2. Deep Dive Phase (1 hour)
Study the code:
- [ProductSearchService.java](src/main/java/com/example/aispring/service/ProductSearchService.java)
- Trace Request → Service → LLM → Response

### 3. System Design Phase (1 hour)
Review:
- [Design Decision Matrix](docs/concepts/design-decisions.md)
- [Interview System Design Q&A](docs/interview/system-design.md)

### 4. Practice Phase (1 hour)
Answer questions:
- [Architecture Q&A](docs/interview/architecture-qa.md)
- [Design Pattern Q&A](docs/interview/design-patterns.md)
- [Behavioral Q&A](docs/interview/behavioral.md)

---

## 🎓 Key Talking Points

### "Tell me about your AI integration experience"
**Answer:**
1. Context: "I created a Spring Boot service that integrates LLMs"
2. Architecture: "AI is in the service layer, not controllers"
3. Design: "Strategy pattern for provider switching"
4. Metrics: "Cost tracking, latency monitoring, cache hit rates"

### "What was the biggest challenge?"
**Good answers:**
- "Latency: LLM calls add 1-5 seconds. Solved with caching (40-60% hit rate)"
- "Cost: $0.001/request adds up. Optimized with semantic caching"
- "Quality: AI responses sometimes wrong. Added validation and fallbacks"

### "How do you measure success?"
**Right metrics:**
- Cost per feature ($/request)
- User impact (CTR, conversion rate)
- Technical metrics (P95 latency, error rate)
- Cache efficiency (hit rate, cost savings)

---

## 🔍 Key Files to Review

| File | Purpose |
|------|---------|
| [AiClient.java](src/main/java/com/example/aispring/client/AiClient.java) | LLM interface - the abstraction layer |
| [ProductSearchService.java](src/main/java/com/example/aispring/service/ProductSearchService.java) | Shows step-by-step AI integration |
| [MockAiClient.java](src/main/java/com/example/aispring/client/MockAiClient.java) | Example implementation |
| [application.yml](src/main/resources/application.yml) | Spring Boot configuration |

---

## 🎯 Design Principles

This project demonstrates **SOLID principles**:

- **S**ingle Responsibility: Each class has one reason to change
- **O**pen/Closed: Open for extension (new AI providers), closed for modification
- **L**iskov Substitution: Any AiClient implementation can be swapped
- **I**nterface Segregation: AiClient interface is focused and minimal
- **D**ependency Inversion: Spring DI manages dependencies, not constructors

---

## 🚀 Next Steps

1. **Run the app**: `mvn spring-boot:run`
2. **Test the APIs**: `bash test-api.sh`
3. **Read the docs**: `mkdocs serve` → http://localhost:8000
4. **Study the code**: Review ProductSearchService.java
5. **Extend it**: Add a new use case or LLM provider

---

## 📞 Support

For questions or issues:
1. Check the [FAQ in docs](docs/future/skills-prompts-agents.md)
2. Review [Best Practices](docs/implementation/best-practices.md)
3. See [Error Handling Guide](docs/implementation/error-handling.md)
4. Check application logs (DEBUG level enabled)

---

**Remember:** AI is just another tool in your service layer. Treat it like any other external dependency - with proper abstraction and dependency injection! 🚀

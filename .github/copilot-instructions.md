# Spring Boot AI Integration Demo Project

## Overview
This project demonstrates how to integrate AI (LLM) capabilities into a Spring Boot service layer without redesigning the entire system. It showcases three practical use cases: AI-enhanced product search, intelligent customer support, and personalized recommendations.

## Architecture
- Client → Controller → Service (AI Orchestrator) → Data/APIs → Response
- AI is integrated as an internal service layer enhancement, not a separate system

## Project Setup Checklist

- [x] Create project structure and pom.xml
- [x] Create core Spring Boot application and configuration
- [x] Create Controllers (ProductSearch, Support, Recommendation)
- [x] Create Services with AI integration
- [x] Create Models and DTOs
- [x] Create AI client abstraction layer
- [x] Build and verify project compiles
- [x] Verify application launches without errors
- [x] Initialize sample data automatically on startup
- [x] Complete documentation (README.md)

## Development Notes
- Uses Spring Boot 3.x with Maven
- Includes mock AI implementation (easily replaceable with OpenAI, Ollama, or Anthropic)
- Demonstrates clean separation of concerns
- Each use case is independent but uses same underlying AI orchestration pattern

---

## 🚀 Future Enhancements Framework

### Available Skills
This project uses Copilot Skills for different domains:
- **llm-integration**: Add new LLM providers and multi-model support
- **performance-optimization**: Caching strategies, batching, async patterns
- **testing-strategy**: Unit tests, integration tests, contract testing
- **observability**: Metrics, logging, tracing, alerting
- **documentation**: MkDocs content, architecture diagrams
- **error-handling**: Fallbacks, retry logic, circuit breakers

### Using Skills
When working on features related to a skill domain:
```
Read the skill file first: .github/skills/[domain]/SKILL.md
Follow guidelines and patterns before implementing
```

### Available Agents
Different agents handle specialized tasks:
- **Explore**: Fast codebase exploration and Q&A
- **mkdocs-content**: Documentation structure and content
- **llm-integration**: Provider integration and multi-model setup
- **performance-tuning**: Caching, batching, optimization
- **test-generation**: Unit and integration test creation

### Prompt Templates
Use these templates for future work:
- `prompts/add-provider.md`: Adding new LLM provider
- `prompts/add-cache-layer.md`: Adding caching functionality
- `prompts/add-use-case.md`: Creating new use case
- `prompts/optimize-performance.md`: Performance improvements
- `prompts/add-observability.md`: Metrics and monitoring

---

## Guidelines for Future Development

### 1. Before Starting Any Feature
1. Check if a skill exists for this domain
2. Read the relevant skill file
3. Review the decision matrix in design-decisions.md
4. Check for similar implementations (Product Search, Support, Recommendations)

### 2. Adding New Features
- Always use Service Layer pattern (not controller-level AI)
- Create interface for new abstractions (like AiClient)
- Follow existing DTO/Model patterns
- Add comprehensive error handling and fallbacks
- Include monitoring hooks (cost, latency, errors)

### 3. Documentation
- Update docs/ with Architecture Diagram (Mermaid)
- Add decision matrix entry
- Create interview Q&A for new pattern
- Document cost-benefit analysis
- Include monitoring strategy

### 4. Code Quality
- All Java files must compile (mvn clean compile)
- No external API calls in tests (use MockAiClient pattern)
- Every LLM call wrapped in try-catch with fallback
- Cost tracking on every AI operation
- Structured logging for observability

---

## Extension Points

### Easy to Extend
- **LLM Providers**: Add new AiClient implementation
- **Use Cases**: Add new Service + Controller + DTOs
- **Caching**: Add cache layer to any service
- **Features**: Shopping cart AI, review analysis, demand forecasting

### Multi-Feature Coordination
- All services inherit from AiClient interface
- All services respect ErrorHandler and CostTracker
- All services implement monitoring hooks
- All services have fallback to traditional method

---

## When to Use Different Agents/Skills

| Task | Agent | Skill | Prompt |
|------|-------|-------|--------|
| Add OpenAI provider | llm-integration | llm-integration | add-provider.md |
| Optimize latency | performance-tuning | performance-optimization | optimize-performance.md |
| Add new use case | mkdocs-content | (multi) | add-use-case.md |
| Write tests | (none) | testing-strategy | (use skill) |
| Add cost tracking | (none) | observability | add-observability.md |
| Explore codebase | Explore | (context building) | (ask directly) |
| Update docs | mkdocs-content | documentation | (ask directly) |

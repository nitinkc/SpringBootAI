# Testing Strategy Skill

## Purpose
Comprehensive testing approach for AI-integrated systems without external API calls.

## Testing Pyramid

```
          Manual Testing (5%)
       Integration Tests (15%)
    Unit Tests (80%)
```

## Unit Testing (80%)

### Core Pattern: MockAiClient
```java
@SpringBootTest
class ProductSearchServiceTest {
    
    @MockBean
    private AiClient aiClient;
    
    @Autowired
    private ProductSearchService service;
    
    @Test
    void testAiEnhancement() {
        // Arrange: Mock AI response
        when(aiClient.generateResponse(anyString()))
            .thenReturn("[{rank:1,id:1},{rank:2,id:2}]");
        
        // Act: Call service
        ProductSearchResponse result = service.search(request);
        
        // Assert: Verify behavior
        assertTrue(result.isAiEnhanced());
        assertEquals(2, result.getResults().size());
        verify(aiClient).generateResponse(any());
    }
}
```

### Test Categories

1. **Happy Path** (60%)
   - Normal request → correct response
   - All data flows correctly

2. **Edge Cases** (20%)
   - Empty query → handled gracefully
   - Very long input → truncated
   - Special characters → escaped

3. **Error Cases** (20%)
   - AI timeout → fallback used
   - Invalid response → validation fails
   - Network error → retry logic

## Integration Testing (15%)

### Real Database, Mock AI
```java
@SpringBootTest
class ProductSearchIntegrationTest {
    
    @Autowired
    private TestRestTemplate rest;
    
    @MockBean
    private AiClient aiClient;
    
    @Test
    void testEndToEnd() {
        when(aiClient.generateResponse(anyString()))
            .thenReturn("[...]");
        
        ResponseEntity<ProductSearchResponse> response = 
            rest.postForEntity("/api/products/search", request, 
                ProductSearchResponse.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
```

## Manual Testing (5%)
- Start app: `mvn spring-boot:run`
- Test API: `./test-api.sh`
- Check dashboards: Logs, metrics, traces
- Verify user experience

## Test Data Strategy

### Fixtures
```java
private ProductSearchRequest createDefaultRequest() {
    return ProductSearchRequest.builder()
        .query("laptop")
        .category("electronics")
        .build();
}
```

### Sample Data (for integration tests)
```java
@BeforeEach
public void setup() {
    // Create test products
    Product laptop = Product.builder()
        .name("Test Laptop")
        .price(1000)
        .build();
    productRepo.save(laptop);
}
```

## What NOT to Test
- Third-party library code (Spring Data, Jackson, etc.)
- SQL queries (trust Hibernate)
- UI rendering (not in backend)

## What to Always Test
- AI integration points (fallbacks, errors)
- Cost calculation logic
- Business rules validation
- Input/output transformation
- Error handling paths

## Coverage Goals
- Unit: 80%+ coverage
- Integration: Key user flows
- Manual: Smoke test each feature

---

**Next**: Use test helpers from `src/test/java/helpers/`

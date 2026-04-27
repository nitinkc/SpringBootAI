package com.example.aispring.client;

import org.springframework.stereotype.Component;

/**
 * Mock AI Client - demonstrates the integration without needing a real LLM
 * 
 * In production, replace this with:
 * - OpenAiClient (OpenAI API)
 * - AnthropicClient (Claude API)
 * - OllamaClient (Local LLM)
 * 
 * This implementation shows the contract/interface is all that's needed
 * in the service layer. The actual implementation can be swapped.
 */
@Component
public class MockAiClient implements AiClient {
    
    private static final String MODEL_NAME = "MockAI-v1.0";
    
    @Override
    public String generateResponse(String prompt) {
        return generateMockResponse(prompt);
    }
    
    @Override
    public String generateResponseWithContext(String systemPrompt, String userPrompt) {
        String combinedPrompt = systemPrompt + "\n\nUser Request: " + userPrompt;
        return generateMockResponse(combinedPrompt);
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
    
    @Override
    public String getModelName() {
        return MODEL_NAME;
    }
    
    /**
     * Simulate LLM responses based on keywords
     * In real implementation, this would call OpenAI, Claude, Ollama, etc.
     */
    private String generateMockResponse(String prompt) {
        
        // Mock responses based on context
        if (prompt.toLowerCase().contains("search")) {
            return "Based on your search, I found the following relevant products that match your interests: " +
                   "Premium laptops are ideal for programming and video editing. " +
                   "Consider checking our latest models with RTX 4080 for 30% better performance than previous generation.";
        }
        
        if (prompt.toLowerCase().contains("support") || prompt.toLowerCase().contains("issue")) {
            return "Thank you for contacting our support team. I understand you're experiencing an issue. " +
                   "Based on similar cases, I recommend: 1) Restart the application, 2) Clear your browser cache, " +
                   "3) Try a different browser. If the issue persists, please provide your order number and we'll " +
                   "escalate to our specialist team within 24 hours.";
        }
        
        if (prompt.toLowerCase().contains("recommend")) {
            return "Based on your purchase history and browsing behavior, I recommend: " +
                   "Premium Noise-Canceling Headphones (matches your tech interest), " +
                   "Professional Monitor Stand (complements your setup), " +
                   "USB-C Hub with Power Delivery (practical for your workflow). " +
                   "These items are frequently purchased together by customers with similar profiles.";
        }
        
        // Default response
        return "I've analyzed your request using advanced language processing. " +
               "The most relevant information has been identified and presented above. " +
               "Is there anything else you'd like to know?";
    }
}

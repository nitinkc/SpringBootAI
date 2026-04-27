package com.example.aispring.client;

/**
 * Abstract interface for AI/LLM client
 * 
 * This allows easy switching between different LLM providers:
 * - OpenAI (GPT-4, GPT-3.5)
 * - Anthropic Claude
 * - Local Ollama
 * - Google PaLM
 * - Or any other LLM service
 */
public interface AiClient {
    
    /**
     * Generate a response from the LLM
     * @param prompt The prompt to send to the LLM
     * @return The LLM's response
     */
    String generateResponse(String prompt);
    
    /**
     * Generate a response with context about the system
     * @param systemPrompt System context/instructions for the LLM
     * @param userPrompt The user's request
     * @return The LLM's response
     */
    String generateResponseWithContext(String systemPrompt, String userPrompt);
    
    /**
     * Check if the AI client is available
     * @return true if the LLM is accessible, false otherwise
     */
    boolean isAvailable();
    
    /**
     * Get the name of the AI model being used
     * @return Model name/identifier
     */
    String getModelName();
}

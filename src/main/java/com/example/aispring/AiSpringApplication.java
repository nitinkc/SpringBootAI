package com.example.aispring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI Spring Boot Demo Application
 * 
 * Demonstrates how to integrate AI/LLM capabilities into the service layer
 * without redesigning the entire system.
 * 
 * Traditional Flow: Client → Controller → Service → DB/APIs → Response
 * Enhanced Flow:   Client → Controller → Service (AI Orchestrator) → Data/APIs + AI → Response
 */
@SpringBootApplication
public class AiSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiSpringApplication.class, args);
    }

}

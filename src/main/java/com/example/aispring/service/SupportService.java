package com.example.aispring.service;

import com.example.aispring.client.AiClient;
import com.example.aispring.dto.SupportTicketRequest;
import com.example.aispring.dto.SupportTicketResponse;
import com.example.aispring.model.SupportTicket;
import com.example.aispring.repository.SupportTicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Support Service with AI-Powered Auto-Response
 * 
 * Traditional Flow: Receive ticket → Save to DB → Return confirmation
 * AI-Enhanced Flow: Receive ticket → Save to DB → Call LLM for auto-response → Save response → Return to customer
 * 
 * Demonstrates how AI can provide immediate responses without redesigning the support system.
 */
@Service
public class SupportService {
    
    private final SupportTicketRepository supportTicketRepository;
    private final AiClient aiClient;
    
    public SupportService(SupportTicketRepository supportTicketRepository, AiClient aiClient) {
        this.supportTicketRepository = supportTicketRepository;
        this.aiClient = aiClient;
    }
    
    /**
     * Create support ticket and generate AI response
     * 
     * The service layer:
     * 1. Creates and saves the ticket
     * 2. Builds context about the issue
     * 3. Calls the LLM to generate an intelligent response
     * 4. Saves the response
     * 5. Returns the enhanced ticket
     */
    public SupportTicketResponse createTicketWithAiResponse(SupportTicketRequest request) {
        long startTime = System.currentTimeMillis();
        
        // Step 1: Save the ticket to database
        SupportTicket ticket = new SupportTicket(
            request.getCustomerName(),
            request.getEmail(),
            request.getIssue()
        );
        ticket = supportTicketRepository.save(ticket);
        
        // Step 2: Build context for AI
        String context = buildTicketContext(ticket);
        
        // Step 3: Call the LLM for intelligent response
        String aiResponse = generateAiResponse(context);
        
        // Step 4: Update ticket with AI response
        ticket.setAiResponse(aiResponse);
        ticket.setStatus(SupportTicket.TicketStatus.ASSIGNED);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket = supportTicketRepository.save(ticket);
        
        long processingTime = System.currentTimeMillis() - startTime;
        
        // Step 5: Transform to response DTO
        return buildResponse(ticket, processingTime);
    }
    
    /**
     * Step 2: Build context for the LLM
     */
    private String buildTicketContext(SupportTicket ticket) {
        return "Support Ticket Context:\n" +
               "- Ticket ID: " + ticket.getId() + "\n" +
               "- Customer: " + ticket.getCustomerName() + "\n" +
               "- Email: " + ticket.getEmail() + "\n" +
               "- Issue: " + ticket.getIssue() + "\n" +
               "- Created: " + ticket.getCreatedAt() + "\n";
    }
    
    /**
     * Step 3: Call the LLM to generate intelligent response
     * This is the AI integration inside the service layer
     */
    private String generateAiResponse(String context) {
        String systemPrompt = "You are a helpful, empathetic customer support agent. " +
                             "Your goal is to understand the customer's issue and provide a helpful, " +
                             "clear response that either solves the problem or escalates appropriately. " +
                             "Be concise but comprehensive. Use simple language.";
        
        String userPrompt = context + 
                           "\nBased on the support ticket above, generate a helpful response " +
                           "that addresses the customer's issue. If you need more information, " +
                           "ask for it. Include troubleshooting steps if applicable.";
        
        if (aiClient.isAvailable()) {
            return aiClient.generateResponseWithContext(systemPrompt, userPrompt);
        } else {
            return "Thank you for contacting support. Your ticket has been received and will be reviewed shortly.";
        }
    }
    
    /**
     * Step 5: Build response DTO
     */
    private SupportTicketResponse buildResponse(SupportTicket ticket, long processingTime) {
        return new SupportTicketResponse(
            ticket.getId(),
            ticket.getCustomerName(),
            ticket.getStatus().toString(),
            ticket.getIssue(),
            ticket.getAiResponse(),
            aiClient.getModelName(),
            processingTime
        );
    }
    
    /**
     * Get ticket by ID
     */
    public SupportTicketResponse getTicket(Long ticketId) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        return new SupportTicketResponse(
            ticket.getId(),
            ticket.getCustomerName(),
            ticket.getStatus().toString(),
            ticket.getIssue(),
            ticket.getAiResponse(),
            aiClient.getModelName(),
            0L
        );
    }
}

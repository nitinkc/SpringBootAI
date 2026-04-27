package com.example.aispring.controller;

import com.example.aispring.dto.SupportTicketRequest;
import com.example.aispring.dto.SupportTicketResponse;
import com.example.aispring.service.SupportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Customer Support
 * 
 * Demonstrates AI-enhanced customer support:
 * Client → Controller → Service (AI auto-response) → DB → Response
 */
@RestController
@RequestMapping("/support/tickets")
public class SupportController {
    
    private final SupportService supportService;
    
    public SupportController(SupportService supportService) {
        this.supportService = supportService;
    }
    
    /**
     * Create a support ticket with AI-powered auto-response
     * 
     * Example:
     * POST /api/support/tickets
     * {
     *   "customerName": "John Doe",
     *   "email": "john@example.com",
     *   "issue": "I can't login to my account, getting 'access denied' error"
     * }
     */
    @PostMapping
    public ResponseEntity<SupportTicketResponse> createTicket(
            @RequestBody SupportTicketRequest request) {
        SupportTicketResponse response = supportService.createTicketWithAiResponse(request);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get ticket by ID
     * 
     * Example:
     * GET /api/support/tickets/1
     */
    @GetMapping("/{ticketId}")
    public ResponseEntity<SupportTicketResponse> getTicket(
            @PathVariable Long ticketId) {
        SupportTicketResponse response = supportService.getTicket(ticketId);
        
        return ResponseEntity.ok(response);
    }
}

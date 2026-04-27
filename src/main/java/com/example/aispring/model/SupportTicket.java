package com.example.aispring.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Support ticket entity for AI-enhanced support
 */
@Entity
@Table(name = "support_tickets")
public class SupportTicket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String customerName;
    private String email;
    private String issue;
    
    @Column(columnDefinition = "TEXT")
    private String aiResponse;
    
    @Enumerated(EnumType.STRING)
    private TicketStatus status;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum TicketStatus {
        OPEN, ASSIGNED, RESOLVED, CLOSED
    }
    
    public SupportTicket() {}
    
    public SupportTicket(String customerName, String email, String issue) {
        this.customerName = customerName;
        this.email = email;
        this.issue = issue;
        this.status = TicketStatus.OPEN;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getIssue() { return issue; }
    public void setIssue(String issue) { this.issue = issue; }

    public String getAiResponse() { return aiResponse; }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

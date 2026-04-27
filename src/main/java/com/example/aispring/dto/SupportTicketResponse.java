package com.example.aispring.dto;

/**
 * Response DTO for support ticket with AI response
 */
public class SupportTicketResponse {
    private Long ticketId;
    private String customerName;
    private String status;
    private String issue;
    private String aiResponse;
    private String aiModel;
    private Long processingTimeMs;
    
    public SupportTicketResponse() {}
    
    public SupportTicketResponse(Long ticketId, String customerName, String status, String issue, String aiResponse, String aiModel, Long processingTimeMs) {
        this.ticketId = ticketId;
        this.customerName = customerName;
        this.status = status;
        this.issue = issue;
        this.aiResponse = aiResponse;
        this.aiModel = aiModel;
        this.processingTimeMs = processingTimeMs;
    }
    
    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getIssue() { return issue; }
    public void setIssue(String issue) { this.issue = issue; }
    
    public String getAiResponse() { return aiResponse; }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }
    
    public String getAiModel() { return aiModel; }
    public void setAiModel(String aiModel) { this.aiModel = aiModel; }
    
    public Long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(Long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
}

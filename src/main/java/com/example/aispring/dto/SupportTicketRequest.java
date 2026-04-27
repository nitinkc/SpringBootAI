package com.example.aispring.dto;

/**
 * Request DTO for support ticket
 */
public class SupportTicketRequest {
    private String customerName;
    private String email;
    private String issue;
    
    public SupportTicketRequest() {}
    
    public SupportTicketRequest(String customerName, String email, String issue) {
        this.customerName = customerName;
        this.email = email;
        this.issue = issue;
    }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getIssue() { return issue; }
    public void setIssue(String issue) { this.issue = issue; }
}

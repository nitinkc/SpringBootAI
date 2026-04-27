package com.example.aispring.model;

import jakarta.persistence.*;

/**
 * User entity for recommendations
 */
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String email;
    
    @Column(columnDefinition = "TEXT")
    private String purchaseHistory;
    
    @Column(columnDefinition = "TEXT")
    private String preferences;
    
    public User() {}
    
    public User(String username, String email, String purchaseHistory, String preferences) {
        this.username = username;
        this.email = email;
        this.purchaseHistory = purchaseHistory;
        this.preferences = preferences;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPurchaseHistory() { return purchaseHistory; }
    public void setPurchaseHistory(String purchaseHistory) { this.purchaseHistory = purchaseHistory; }

    public String getPreferences() { return preferences; }
    public void setPreferences(String preferences) { this.preferences = preferences; }
}

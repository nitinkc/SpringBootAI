package com.example.aispring.repository;

import com.example.aispring.model.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for SupportTicket entity
 */
@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    
    List<SupportTicket> findByStatus(SupportTicket.TicketStatus status);
    
    List<SupportTicket> findByCustomerName(String customerName);
}

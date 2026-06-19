package com.surense.customersupporthub.ticket;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByCustomerId(Long customerId);

    List<Ticket> findByCustomerAgentId(Long agentId);
   
    List<Ticket> findByCustomerAgentIdAndStatus(
            Long agentId,
            TicketStatus status);

    List<Ticket> findByStatus(
            TicketStatus status);
}
package com.surense.customersupporthub.ticket;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.surense.customersupporthub.ticket.dto.CreateTicketRequest;
import com.surense.customersupporthub.ticket.dto.TicketResponse;

import com.surense.customersupporthub.ticket.dto.UpdateTicketStatusRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }
    
    @PutMapping("/{id}/assign/{agentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public TicketResponse assignTicketToAgent(
            @PathVariable Long id,
            @PathVariable Long agentId) {

        return ticketService.assignTicketToAgent(id, agentId);
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    public TicketResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketStatusRequest request) {

        return ticketService.updateStatus(
                id,
                request.status()
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse createTicket(
            @Valid @RequestBody CreateTicketRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        return ticketService.createTicket(request, jwt.getSubject());
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<TicketResponse> getMyTickets(
            @AuthenticationPrincipal Jwt jwt) {

        return ticketService.getMyTickets(jwt.getSubject());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','AGENT','ADMIN')")
    public TicketResponse getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('AGENT','ADMIN')")
    public List<TicketResponse> getTickets(
            @RequestParam(required = false) TicketStatus status,
            @AuthenticationPrincipal Jwt jwt) {

        return ticketService.getTickets(
                jwt.getSubject(),
                jwt.getClaimAsString("role"),
                status
        );
    }
}
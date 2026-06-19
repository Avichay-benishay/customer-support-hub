package com.surense.customersupporthub.ticket;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.surense.customersupporthub.exception.UnauthorizedException;
import com.surense.customersupporthub.ticket.dto.CreateTicketRequest;
import com.surense.customersupporthub.ticket.dto.TicketResponse;
import com.surense.customersupporthub.user.User;
import com.surense.customersupporthub.user.UserRepository;
import com.surense.customersupporthub.exception.NotFoundException;

import com.surense.customersupporthub.exception.BadRequestException;
import org.springframework.transaction.annotation.Transactional;
import com.surense.customersupporthub.user.Role;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketService(
            TicketRepository ticketRepository,
            UserRepository userRepository) {

        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    public TicketResponse createTicket(
            CreateTicketRequest request,
            String username) {

        User customer = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));

        Ticket ticket = new Ticket();
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setCustomer(customer);

        return toResponse(ticketRepository.save(ticket));
    }

    public List<TicketResponse> getMyTickets(String username) {

        User customer = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));

        return ticketRepository.findByCustomerId(customer.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TicketResponse> getTickets(
            String username,
            String role,
            TicketStatus status) {

    	if ("ADMIN".equals(role)) {

    	    List<Ticket> tickets = status == null
    	            ? ticketRepository.findAll()
    	            : ticketRepository.findByStatus(status);

    	    return tickets.stream()
    	            .map(this::toResponse)
    	            .toList();
    	}

    	User agent = userRepository.findByUsername(username)
    	        .orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));

    	List<Ticket> tickets = status == null
    	        ? ticketRepository.findByCustomerAgentId(agent.getId())
    	        : ticketRepository.findByCustomerAgentIdAndStatus(agent.getId(), status);

    	return tickets.stream()
    	        .map(this::toResponse)
    	        .toList();
    }
    public TicketResponse getTicketById(Long ticketId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket not found"));

        return toResponse(ticket);
    }
    
    @Transactional
    public TicketResponse assignTicketToAgent(
            Long ticketId,
            Long agentId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket not found"));

        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new NotFoundException("Agent not found"));

        if (agent.getRole() != Role.AGENT) {
            throw new BadRequestException("User is not an agent");
        }

        ticket.getCustomer().setAgent(agent);

        userRepository.save(ticket.getCustomer());

        return toResponse(ticket);
    }
    
    public TicketResponse updateStatus(
            Long ticketId,
            TicketStatus status) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket not found"));

        ticket.setStatus(status);

        return toResponse(ticketRepository.save(ticket));
    }

    private TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus().name(),
                ticket.getCreatedAt()
        );
    }
}
package com.surense.customersupporthub.ticket;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.surense.customersupporthub.exception.BadRequestException;
import com.surense.customersupporthub.exception.NotFoundException;
import com.surense.customersupporthub.exception.UnauthorizedException;
import com.surense.customersupporthub.ticket.dto.CreateTicketRequest;
import com.surense.customersupporthub.ticket.dto.TicketResponse;
import com.surense.customersupporthub.user.Role;
import com.surense.customersupporthub.user.User;
import com.surense.customersupporthub.user.UserRepository;
import org.springframework.security.access.AccessDeniedException;

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

    @Transactional
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

    @Transactional(readOnly = true)
    public List<TicketResponse> getMyTickets(String username) {

        User customer = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));

        return ticketRepository.findByCustomerId(customer.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public TicketResponse getTicketById(
            Long ticketId,
            String username,
            String role) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket not found"));

        validateAccess(ticket, username, role);

        return toResponse(ticket);
    }

    @Transactional
    public TicketResponse updateStatus(
            Long ticketId,
            TicketStatus status,
            String username,
            String role) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket not found"));

        validateAccess(ticket, username, role);

        ticket.setStatus(status);

        return toResponse(ticketRepository.save(ticket));
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

        User customer = ticket.getCustomer();
        customer.setAgent(agent);

        userRepository.save(customer);

        return toResponse(ticket);
    }

    private void validateAccess(
            Ticket ticket,
            String username,
            String role) {

        if ("ADMIN".equals(role)) {
            return;
        }

        User authenticatedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));

        if ("CUSTOMER".equals(role)) {
            if (!ticket.getCustomer().getId().equals(authenticatedUser.getId())) {
            	throw new AccessDeniedException("Access denied");
            }
            return;
        }

        if ("AGENT".equals(role)) {
            User customer = ticket.getCustomer();

            if (customer.getAgent() == null ||
                    !customer.getAgent().getId().equals(authenticatedUser.getId())) {
            	throw new AccessDeniedException("Access denied");
            }
            return;
        }

        throw new AccessDeniedException("Access denied");
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

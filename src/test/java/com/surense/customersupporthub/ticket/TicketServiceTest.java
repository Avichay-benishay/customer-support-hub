package com.surense.customersupporthub.ticket;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Optional;

import com.surense.customersupporthub.exception.NotFoundException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.surense.customersupporthub.ticket.dto.CreateTicketRequest;
import com.surense.customersupporthub.ticket.dto.TicketResponse;
import com.surense.customersupporthub.user.Role;
import com.surense.customersupporthub.user.User;
import com.surense.customersupporthub.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void createTicket_shouldCreateOpenTicketForCustomer() {
        User customer = new User();
        customer.setId(1L);
        customer.setUsername("customer1");
        customer.setRole(Role.CUSTOMER);

        when(userRepository.findByUsername("customer1"))
                .thenReturn(Optional.of(customer));

        when(ticketRepository.save(any(Ticket.class)))
                .thenAnswer(invocation -> {
                    Ticket ticket = invocation.getArgument(0);
                    ticket.setId(10L);
                    return ticket;
                });

        CreateTicketRequest request = new CreateTicketRequest(
                "Login issue",
                "I cannot login to the system"
        );

        TicketResponse response = ticketService.createTicket(request, "customer1");

        assertEquals(10L, response.id());
        assertEquals("Login issue", response.title());
        assertEquals("OPEN", response.status());
    }
    @Test
    void getTicketById_shouldThrowNotFoundWhenTicketDoesNotExist() {

        when(ticketRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> ticketService.getTicketById(999L)
        );
    }
}
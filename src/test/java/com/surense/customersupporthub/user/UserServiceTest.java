package com.surense.customersupporthub.user;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.surense.customersupporthub.exception.ConflictException;
import com.surense.customersupporthub.user.dto.CreateCustomerRequest;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createCustomer_shouldThrowConflictWhenUsernameExists() {

        CreateCustomerRequest request =
                new CreateCustomerRequest(
                        "customer1",
                        "password123",
                        "Customer One",
                        "customer1@test.com",
                        null
                );

        when(userRepository.existsByUsername("customer1"))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> userService.createCustomer(
                        request,
                        "agent",
                        "AGENT"
                )
        );
    }

    @Test
    void createCustomer_shouldThrowConflictWhenEmailExists() {

        CreateCustomerRequest request =
                new CreateCustomerRequest(
                		"customer1",
                        "password123",
                        "Customer One",
                        "customer1@test.com",
                        null
                );

        when(userRepository.existsByUsername("customer1"))
                .thenReturn(false);

        when(userRepository.existsByEmail("customer1@test.com"))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> userService.createCustomer(
                        request,
                        "agent",
                        "AGENT"
                )
        );
    }
}
package com.surense.customersupporthub.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.surense.customersupporthub.user.UserController;
import com.surense.customersupporthub.user.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void customerCannotQueryAllCustomers() throws Exception {
        mockMvc.perform(get("/api/customers")
                        .with(jwt().jwt(jwt -> jwt
                                .subject("customer1")
                                .claim("role", "CUSTOMER"))))
                .andExpect(status().isForbidden());
    }
}
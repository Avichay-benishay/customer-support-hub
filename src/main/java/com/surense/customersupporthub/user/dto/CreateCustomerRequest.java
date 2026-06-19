package com.surense.customersupporthub.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(
        @NotBlank
        String username,

        @NotBlank
        @Size(min = 6)
        String password,

        @NotBlank
        String name,

        @NotBlank
        @Email
        String email,

        Long agentId
) {
}
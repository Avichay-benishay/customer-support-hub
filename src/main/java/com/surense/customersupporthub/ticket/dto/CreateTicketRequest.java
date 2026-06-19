package com.surense.customersupporthub.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTicketRequest(

        @NotBlank
        String title,

        @NotBlank
        @Size(min = 5, max = 2000)
        String description
) {
}
package com.surense.customersupporthub.ticket.dto;

import com.surense.customersupporthub.ticket.TicketStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateTicketStatusRequest(
        @NotNull
        TicketStatus status
) {
}
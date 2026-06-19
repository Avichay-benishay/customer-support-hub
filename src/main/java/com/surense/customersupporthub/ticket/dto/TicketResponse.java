package com.surense.customersupporthub.ticket.dto;

import java.time.LocalDateTime;

public record TicketResponse(
        Long id,
        String title,
        String description,
        String status,
        LocalDateTime createdAt
) {
}
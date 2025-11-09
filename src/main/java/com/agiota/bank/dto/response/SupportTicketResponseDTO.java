package com.agiota.bank.dto.response;

import com.agiota.bank.model.supportticket.SupportTicketStatus;

import java.time.LocalDateTime;

public record SupportTicketResponseDTO(
        Long id,
        Long userId,
        String title,
        String description,
        SupportTicketStatus status,
        String response,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

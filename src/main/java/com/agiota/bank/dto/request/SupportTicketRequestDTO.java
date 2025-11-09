package com.agiota.bank.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SupportTicketRequestDTO(
        @NotBlank
        String title,

        @NotBlank
        String description
) {
}

package com.agiota.bank.dto.request;

import com.agiota.bank.model.supportticket.SupportTicketStatus;

public record UpdateSupportTicketRequestDTO(
        SupportTicketStatus status,
        String response
) {
}

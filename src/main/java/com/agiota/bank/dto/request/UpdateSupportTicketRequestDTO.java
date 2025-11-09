package com.agiota.bank.dto.request;

import com.agiota.bank.model.supportticket.SupportTicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSupportTicketRequestDTO {

    private SupportTicketStatus status;

    private String response;
}

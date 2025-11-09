package com.agiota.bank.dto.response;

import com.agiota.bank.model.supportticket.SupportTicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupportTicketResponseDTO {

    private Long id;
    private Long userId;
    private String title;
    private String description;
    private SupportTicketStatus status;
    private String response;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

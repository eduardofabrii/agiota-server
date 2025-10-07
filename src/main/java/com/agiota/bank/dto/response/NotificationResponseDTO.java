package com.agiota.bank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {
    private Long id;
    private Long recipientId;
    private String recipientName;
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead;
}
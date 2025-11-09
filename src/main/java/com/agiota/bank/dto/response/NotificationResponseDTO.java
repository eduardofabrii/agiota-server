package com.agiota.bank.dto.response;

import com.agiota.bank.model.notification.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {
    private Long id;
    private Long user_id;
    private String userName;
    private String message;
    private NotificationType type;
    private LocalDateTime createdAt;
    private boolean isRead;
}
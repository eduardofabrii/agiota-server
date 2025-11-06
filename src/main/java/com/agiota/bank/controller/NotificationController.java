package com.agiota.bank.controller;

import com.agiota.bank.dto.request.NotificationRequestDTO;
import com.agiota.bank.dto.response.NotificationResponseDTO;
import com.agiota.bank.service.notification.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.agiota.bank.model.user.User;

import java.util.List;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificações", description = "Operações relacionadas às notificações")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @Operation(summary = "Criar nova notificação")
    public ResponseEntity<NotificationResponseDTO> createNotification(@Valid @RequestBody NotificationRequestDTO notificationRequest) {
        NotificationResponseDTO createdNotification = notificationService.createAndSendNotification(notificationRequest);
        return ResponseEntity.status(201).body(createdNotification);
    }

    @GetMapping
    @Operation(summary = "Listar notificações do usuário")
    public ResponseEntity<List<NotificationResponseDTO>> getUserNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        // O @AuthenticationPrincipal injeta o usuário logado
        User currentUser = (User) userDetails;
        List<NotificationResponseDTO> notifications = notificationService.getNotificationsForUser(currentUser.getId());
        return ResponseEntity.ok(notifications);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir notificação")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
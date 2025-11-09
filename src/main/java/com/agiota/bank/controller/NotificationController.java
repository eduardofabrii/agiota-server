package com.agiota.bank.controller;

import com.agiota.bank.dto.request.NotificationRequestDTO;
import com.agiota.bank.dto.response.NotificationResponseDTO;
import com.agiota.bank.exception.CustomException;
import com.agiota.bank.service.notification.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.agiota.bank.model.user.User;
import com.agiota.bank.model.user.UserRole;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;


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
    @Operation(summary = "Listar notificações dos usuários")
    public ResponseEntity<List<NotificationResponseDTO>> getUserNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = (User) userDetails;
        List<NotificationResponseDTO> notifications = notificationService.getNotificationsForUser(currentUser.getId());
        return ResponseEntity.ok(notifications);
    }


    @GetMapping("/all")
    @Operation(summary = "Listar todas as notificações (somente administradores)")
    public ResponseEntity<List<NotificationResponseDTO>> getAllNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = (User) userDetails;
        
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new CustomException("Acesso negado. Apenas administradores podem acessar todas as notificações.", HttpStatus.FORBIDDEN.value());
        }
        
        List<NotificationResponseDTO> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar notificações de um usuário específico (para testes)")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsForUser(@PathVariable Long userId) {
        List<NotificationResponseDTO> notifications = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

   

    @PutMapping("/user/{userId}/")
    @Operation(summary = "Editar notificações apenas por ter mais uma função")
    public ResponseEntity<List<NotificationResponseDTO>> markAllNotificationsAsRead(@PathVariable Long userId) {
        List<NotificationResponseDTO> updatedNotifications = notificationService.updatedNotifications(userId);
        return ResponseEntity.ok(updatedNotifications);
    }

    //mostrar notificação por id

    @GetMapping("/{id}")
    @Operation(summary = "Mostrar notificação por ID")
    public ResponseEntity<NotificationResponseDTO> getNotificationById(@PathVariable Long id) {
        NotificationResponseDTO notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notification);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir notificação")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

   
}
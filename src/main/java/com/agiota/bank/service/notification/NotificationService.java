package com.agiota.bank.service.notification;

import com.agiota.bank.dto.request.NotificationRequestDTO;
import com.agiota.bank.dto.response.NotificationResponseDTO;
import com.agiota.bank.model.notification.Notification;
import com.agiota.bank.model.user.User;
import com.agiota.bank.repository.NotificationRepository;
import com.agiota.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    public NotificationResponseDTO createAndSendNotification(NotificationRequestDTO requestDTO) {
        User recipient = userRepository.findById(requestDTO.getUser_id())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        Notification notification = new Notification(recipient, requestDTO.getMessage());
        notification = notificationRepository.save(notification);

        // Enviar email se subject foi fornecido
        if (requestDTO.getSubject() != null && !requestDTO.getSubject().trim().isEmpty()) {
            sendEmailNotification(recipient.getEmail(), requestDTO.getSubject(), requestDTO.getMessage());
        }

        return convertToResponseDTO(notification);
    }

    public void createAndSendNotification(User recipient, String subject, String message) {
        Notification notification = new Notification(recipient, message);
        notificationRepository.save(notification);

        sendEmailNotification(recipient.getEmail(), subject, message);
    }

    private void sendEmailNotification(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@agiotabank.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail: " + e.getMessage());
        }
    }

    public List<NotificationResponseDTO> getNotificationsForUser(Long userId) {
        List<Notification> notifications = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new RuntimeException("Notificação não encontrada");
        }
        notificationRepository.deleteById(notificationId);
    }

    private NotificationResponseDTO convertToResponseDTO(Notification notification) {
        return new NotificationResponseDTO(
                notification.getId(),
                notification.getRecipient().getId(),
                notification.getRecipient().getName(),
                notification.getMessage(),
                notification.getCreatedAt(),
                notification.isRead()
        );
    }
}
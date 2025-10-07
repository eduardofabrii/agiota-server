package com.agiota.bank.service.notification;

import com.agiota.bank.model.notification.Notification;
import com.agiota.bank.model.user.User;
import com.agiota.bank.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

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

    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new RuntimeException("Notificação não encontrada");
        }
        notificationRepository.deleteById(notificationId);
    }
}
package com.agiota.bank.service.notification;

import com.agiota.bank.dto.request.NotificationRequestDTO;
import com.agiota.bank.dto.response.NotificationResponseDTO;
import com.agiota.bank.model.notification.Notification;
import com.agiota.bank.model.user.User;
import com.agiota.bank.repository.NotificationRepository;
import com.agiota.bank.repository.UserRepository;
import com.agiota.bank.service.notification.NotificationMessageTemplate.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${mailtrap.api.token:c6ce3d70955d3f03a69b5eb719c1eb79}")
    private String mailtrapApiToken;

    @Value("${mailtrap.sandbox.name:agiotaBankSmtp}")
    private String mailtrapSandboxName;

    public NotificationResponseDTO createAndSendNotification(NotificationRequestDTO requestDTO) {
        User recipient = userRepository.findById(requestDTO.getUser_id())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        Notification notification = new Notification(recipient, requestDTO.getMessage());
        notification = notificationRepository.save(notification);

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


    /**
     * Notificação para criação de conta
     */
    public void notifyAccountCreated(User user, String accountNumber, String agency) {
        NotificationMessage template = NotificationMessageTemplate.createAccountMessage(accountNumber, agency);
        createNotificationWithType(user, template);
    }

    /**
     * Notificação para atualização de conta
     */
    public void notifyAccountUpdated(User user, String accountNumber) {
        NotificationMessage template = NotificationMessageTemplate.updateAccountMessage(accountNumber);
        createNotificationWithType(user, template);
    }

    /**
     * Notificação para exclusão de conta
     */
    public void notifyAccountDeleted(User user, String accountNumber) {
        NotificationMessage template = NotificationMessageTemplate.deleteAccountMessage(accountNumber);
        createNotificationWithType(user, template);
    }

    /**
     * Notificação para criação de chave PIX
     */
    public void notifyPixKeyCreated(User user, String pixKey, String keyType) {
        NotificationMessage template = NotificationMessageTemplate.createPixKeyMessage(pixKey, keyType);
        createNotificationWithType(user, template);
    }

    /**
     * Notificação para exclusão de chave PIX
     */
    public void notifyPixKeyDeleted(User user, String pixKey) {
        NotificationMessage template = NotificationMessageTemplate.deletePixKeyMessage(pixKey);
        createNotificationWithType(user, template);
    }

    /**
     * Notificação para transação enviada
     */
    public void notifyTransactionSent(User user, double amount, String destinationInfo, String transactionType) {
        NotificationMessage template = NotificationMessageTemplate.transactionSentMessage(amount, destinationInfo, transactionType);
        createNotificationWithType(user, template);
    }

    /**
     * Notificação para transação recebida
     */
    public void notifyTransactionReceived(User user, double amount, String originInfo, String transactionType) {
        NotificationMessage template = NotificationMessageTemplate.transactionReceivedMessage(amount, originInfo, transactionType);
        createNotificationWithType(user, template);
    }

    /**
     * Notificação para criação de cartão
     */
    public void notifyCardCreated(User user, String cardNumber, String cardType) {
        NotificationMessage template = NotificationMessageTemplate.createCardMessage(cardNumber, cardType);
        createNotificationWithType(user, template);
    }

    /**
     * Notificação para atualização de cartão
     */
    public void notifyCardUpdated(User user, String maskedCardNumber) {
        NotificationMessage template = NotificationMessageTemplate.updateCardMessage(maskedCardNumber);
        createNotificationWithType(user, template);
    }

    /**
     * Notificação para exclusão de cartão
     */
    public void notifyCardDeleted(User user, String maskedCardNumber) {
        NotificationMessage template = NotificationMessageTemplate.deleteCardMessage(maskedCardNumber);
        createNotificationWithType(user, template);
    }

    /**
     * Notificação para adição de beneficiário
     */
    public void notifyBeneficiaryAdded(User user, String beneficiaryName) {
        NotificationMessage template = NotificationMessageTemplate.addBeneficiaryMessage(beneficiaryName);
        createNotificationWithType(user, template);
    }

    /**
     * Notificação para atualização de beneficiário
     */
    public void notifyBeneficiaryUpdated(User user, String beneficiaryName) {
        NotificationMessage template = NotificationMessageTemplate.updateBeneficiaryMessage(beneficiaryName);
        createNotificationWithType(user, template);
    }

    /**
     * Notificação para exclusão de beneficiário
     */
    public void notifyBeneficiaryDeleted(User user, String beneficiaryName) {
        NotificationMessage template = NotificationMessageTemplate.deleteBeneficiaryMessage(beneficiaryName);
        createNotificationWithType(user, template);
    }

    /**
     * Notificação de alerta de segurança
     */
    public void notifySecurityAlert(User user, String action) {
        NotificationMessage template = NotificationMessageTemplate.securityAlertMessage(action);
        createNotificationWithType(user, template);
        log.warn("Alerta de segurança enviado para usuário ID: {} - Ação: {}", user.getId(), action);
    }


    /**
     * Método helper para criar notificação com tipo específico
     */
    private void createNotificationWithType(User user, NotificationMessage template) {
        try {
            Notification notification = new Notification(user, template.getMessage(), template.getType());
            notificationRepository.save(notification);
            
            sendEmailNotification(user.getEmail(), template.getSubject(), template.getMessage());
        } catch (Exception e) {
            log.error("Erro ao enviar notificação {} para usuário {}: {}", template.getType(), user.getEmail(), e.getMessage());
        }
    }

    private void sendEmailNotification(String to, String subject, String text) {
        try {
            
            WebClient webClient = webClientBuilder
                .baseUrl("https://send.api.mailtrap.io")
                .defaultHeader("Authorization", "Bearer " + mailtrapApiToken)
                .defaultHeader("Content-Type", "application/json")
                .build();

            Map<String, Object> emailPayload = new HashMap<>();
            
            Map<String, String> fromAddress = new HashMap<>();
            fromAddress.put("email", "noreply@demomailtrap.com");
            fromAddress.put("name", "Agiota Bank");
            emailPayload.put("from", fromAddress);
            
            Map<String, String> toAddress = new HashMap<>();
            toAddress.put("email", to);
            emailPayload.put("to", List.of(toAddress));
            
            emailPayload.put("subject", subject);
            emailPayload.put("text", text);
            emailPayload.put("category", "Notification System");


            String response = webClient.post()
                .uri("/api/send")
                .bodyValue(emailPayload)
                .retrieve()
                .bodyToMono(String.class)
                .block();

           
            
        } catch (Exception e) {
            log.error("❌ Erro ao enviar e-mail via Mailtrap API para: {} - Erro: {}", to, e.getMessage());
            log.error("❌ Stack trace completo:", e);
            
           
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

    /**
     * Marcar notificação como lida
     */
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    /**
     * Marcar todas as notificações de um usuário como lidas
     */
    public void markAllAsReadForUser(Long userId) {
        List<Notification> notifications = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    /**
     * Método para testar a conexão de email via Mailtrap
     */
    public void testEmailConnection(String email) {
        try {
            log.info("🧪 Testando envio de email via Mailtrap para: {}", email);
            
            String testMessage = "🧪 Email de Teste - Agiota Bank\n\n" +
                    "Este é um email de teste para verificar se a integração com Mailtrap está funcionando.\n\n" +
                    "Se você visualizar este email no sandbox do Mailtrap, o sistema de notificações está funcionando corretamente!\n\n" +
                    "Data/Hora: " + java.time.LocalDateTime.now() + "\n\n" +
                    "Atenciosamente,\n" +
                    "Equipe Agiota Bank";
            
            sendEmailNotification(email, "🧪 Teste de Email - Agiota Bank", testMessage);
            
            
        } catch (Exception e) {
            log.error("❌ Erro ao enviar email de teste para: {} - Erro: {}", email, e.getMessage());
            throw new RuntimeException("Falha no envio do email: " + e.getMessage());
        }
    }

    private NotificationResponseDTO convertToResponseDTO(Notification notification) {
        return new NotificationResponseDTO(
                notification.getId(),
                notification.getRecipient().getId(),
                notification.getRecipient().getName(),
                notification.getMessage(),
                notification.getType(),
                notification.getCreatedAt(),
                notification.isRead()
        );
    }
}
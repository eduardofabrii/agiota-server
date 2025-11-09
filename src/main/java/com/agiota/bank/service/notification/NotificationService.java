package com.agiota.bank.service.notification;

import com.agiota.bank.dto.request.NotificationRequestDTO;
import com.agiota.bank.dto.response.NotificationResponseDTO;
import com.agiota.bank.model.notification.Notification;
import com.agiota.bank.model.user.User;
import com.agiota.bank.repository.NotificationRepository;
import com.agiota.bank.repository.UserRepository;
import com.agiota.bank.service.notification.NotificationMessageTemplate.NotificationMessage;
import freemarker.template.Configuration; 
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.internet.MimeMessage; 
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper; 
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils; 
import org.springframework.beans.factory.annotation.Value; 

import java.io.IOException;
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
    private final JavaMailSender mailSender;
    
    private final Configuration freemarkerConfig;

    @Value("${spring.mail.username}")
    private String mailFromUsername;

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

   
     public void notifyUserCreated(User user) {
        NotificationMessage template = NotificationMessageTemplate.userCreatedMessage(user.getName());
        createNotificationWithType(user, template);
    }

    public void notifyAccountCreated(User user, String accountNumber, String agency) {
        NotificationMessage template = NotificationMessageTemplate.createAccountMessage(accountNumber, agency);
        createNotificationWithType(user, template);
    }

    public void notifyAccountUpdated(User user, String accountNumber) {
        NotificationMessage template = NotificationMessageTemplate.updateAccountMessage(accountNumber);
        createNotificationWithType(user, template);
    }

    public void notifyAccountDeleted(User user, String accountNumber) {
        NotificationMessage template = NotificationMessageTemplate.deleteAccountMessage(accountNumber);
        createNotificationWithType(user, template);
    }
 
    public void notifyPixKeyCreated(User user, String pixKey, String keyType) {
        NotificationMessage template = NotificationMessageTemplate.createPixKeyMessage(pixKey, keyType);
        createNotificationWithType(user, template);
    }

    public void notifyPixKeyDeleted(User user, String pixKey) {
        NotificationMessage template = NotificationMessageTemplate.deletePixKeyMessage(pixKey);
        createNotificationWithType(user, template);
    }

    public void notifyTransactionSent(User user, double amount, String destinationInfo, String transactionType) {
        NotificationMessage template = NotificationMessageTemplate.transactionSentMessage(amount, destinationInfo, transactionType);
        createNotificationWithType(user, template);
    }

    public void notifyTransactionReceived(User user, double amount, String originInfo, String transactionType) {
        NotificationMessage template = NotificationMessageTemplate.transactionReceivedMessage(amount, originInfo, transactionType);
        createNotificationWithType(user, template);
    }

    public void notifyCardCreated(User user, String cardNumber, String cardType) {
        NotificationMessage template = NotificationMessageTemplate.createCardMessage(cardNumber, cardType);
        createNotificationWithType(user, template);
    }

    public void notifyCardUpdated(User user, String maskedCardNumber) {
        NotificationMessage template = NotificationMessageTemplate.updateCardMessage(maskedCardNumber);
        createNotificationWithType(user, template);
    }

    public void notifyCardDeleted(User user, String maskedCardNumber) {
        NotificationMessage template = NotificationMessageTemplate.deleteCardMessage(maskedCardNumber);
        createNotificationWithType(user, template);
    }

    public void notifyBeneficiaryAdded(User user, String beneficiaryName) {
        NotificationMessage template = NotificationMessageTemplate.addBeneficiaryMessage(beneficiaryName);
        createNotificationWithType(user, template);
    }

    public void notifyBeneficiaryUpdated(User user, String beneficiaryName) {
        NotificationMessage template = NotificationMessageTemplate.updateBeneficiaryMessage(beneficiaryName);
        createNotificationWithType(user, template);
    }

    public void notifyBeneficiaryDeleted(User user, String beneficiaryName) {
        NotificationMessage template = NotificationMessageTemplate.deleteBeneficiaryMessage(beneficiaryName);
        createNotificationWithType(user, template);
    }

    public void notifySecurityAlert(User user, String action) {
        NotificationMessage template = NotificationMessageTemplate.securityAlertMessage(action);
        createNotificationWithType(user, template);
        log.warn("Alerta de segurança enviado para usuário ID: {} - Ação: {}", user.getId(), action);
    }


    private void createNotificationWithType(User user, NotificationMessage template) {
        try {
            Notification notification = new Notification(user, template.getMessage(), template.getType());
            notificationRepository.save(notification);
            
            // Agora isso chama nosso novo método de envio de HTML
            sendEmailNotification(user.getEmail(), template.getSubject(), template.getMessage());
        } catch (Exception e) {
            log.error("Erro ao enviar notificação {} para usuário {}: {}", template.getType(), user.getEmail(), e.getMessage());
        }
    }

    /**
     * MÉTODO sendEmailNotification REFATORADO para usar FTL e MimeMessage
     */
    private void sendEmailNotification(String to, String subject, String text) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart, UTF-8

            // 1. Cria o "modelo" de dados para o template
            Map<String, Object> model = new HashMap<>();
            model.put("subject", subject);
            model.put("messageBody", text); // O 'text' é o texto puro que seu Template.java já cria

            // 2. Processa o template FTL com o modelo
            Template ftlTemplate = freemarkerConfig.getTemplate("email-template.ftl");
            String htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(ftlTemplate, model);

            // 3. Configura o MimeMessage
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = O texto é HTML
            
            // Adiciona um "Nome Amigável" (Mais profissional)
            helper.setFrom(mailFromUsername, "Agiota Bank"); 

            // 4. Envia o e-mail
            mailSender.send(mimeMessage); 

            log.info("✅ E-mail (HTML) enviado com sucesso via SMTP para: {}", to);
            
        } catch (Exception e) { // Pega todas as exceções (Messaging, IOException, TemplateException)
            log.error("❌ Erro ao enviar e-mail (HTML) via SMTP para: {} - Erro: {}", to, e.getMessage());
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

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsReadForUser(Long userId) {
        List<Notification> notifications = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    public void testEmailConnection(String email) {
        try {
            log.info("🧪 Testando envio de email (HTML) via SMTP para: {}", email);
            
            String testMessage = "🧪 Email de Teste - Agiota Bank\n\n" +
                    "Este é um email de teste para verificar se a integração com SMTP e FreeMarker está funcionando.\n\n" +
                    "Se você recebeu este email formatado em HTML, o sistema está funcionando corretamente!\n\n" +
                    "Data/Hora: " + java.time.LocalDateTime.now() + "\n\n" +
                    "Atenciosamente,\n" +
                    "Equipe Agiota Bank";
            
            sendEmailNotification(email, "🧪 Teste de Email (HTML) - Agiota Bank", testMessage);
            
        } catch (Exception e) {
            log.error("❌ Erro ao enviar email de teste HTML para: {} - Erro: {}", email, e.getMessage());
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
package com.agiota.bank.service.notification;

import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.model.notification.Notification;
import com.agiota.bank.model.user.User;
import com.agiota.bank.model.user.UserRole;
import com.agiota.bank.repository.NotificationRepository;
import com.agiota.bank.repository.UserRepository;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private Configuration freemarkerConfig;

    @Mock
    private MimeMessage mockMimeMessage;
    @Mock
    private Template mockTemplate;
    @Mock
    private MimeMessageHelper mockHelper;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User("Test User", "test@example.com", "pass123", UserRole.USER);
        mockUser.setId(1L);
        
        ReflectionTestUtils.setField(notificationService, "mailFromUsername", "test@agiota.com");
    }

    @Test
    void notifyAccountCreated_ShouldSaveToRepoAndSendEmail() throws Exception {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        when(freemarkerConfig.getTemplate("email-template.ftl")).thenReturn(mockTemplate);
        when(notificationRepository.save(any(Notification.class))).thenReturn(new Notification());
        
        doNothing().when(mockTemplate).process(any(), any());
        
        // Act
        notificationService.notifyAccountCreated(mockUser, "12345", "0001");

        // Assert
        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void getNotificationById_ShouldThrowResourceNotFound_WhenIdNotExists() {
        long notificationId = 99L;
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.getNotificationById(notificationId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Notificação não encontrada");

        verify(notificationRepository, times(1)).findById(notificationId);
    }

    @Test
    void deleteNotification_ShouldThrowResourceNotFound_WhenIdNotExists() {
        long notificationId = 99L;
        when(notificationRepository.existsById(notificationId)).thenReturn(false);

        assertThatThrownBy(() -> notificationService.deleteNotification(notificationId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Notificação não encontrada");

        verify(notificationRepository, never()).deleteById(anyLong());
    }

    @Test
    void markAsRead_ShouldThrowResourceNotFound_WhenIdNotExists() {
        long notificationId = 99L;
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markAsRead(notificationId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Notificação não encontrada");

        verify(notificationRepository, never()).save(any());
    }
}
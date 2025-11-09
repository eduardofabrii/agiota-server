package com.agiota.bank.controller;

import com.agiota.bank.dto.response.NotificationResponseDTO;
import com.agiota.bank.exception.CustomException;
import com.agiota.bank.model.user.User;
import com.agiota.bank.model.user.UserRole;
import com.agiota.bank.service.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private User mockUser;
    private User mockAdmin;
    private NotificationResponseDTO mockDto;

    @BeforeEach
    void setUp() {
        mockUser = new User("Test User", "user@example.com", "pass123", UserRole.USER);
        mockUser.setId(1L);

        mockAdmin = new User("Admin User", "admin@example.com", "pass123", UserRole.ADMIN);
        mockAdmin.setId(2L);

        mockDto = new NotificationResponseDTO();
        mockDto.setId(100L);
        mockDto.setMessage("Test message");
        mockDto.setUser_id(1L);
    }

    @Test
    void getUserNotifications_ShouldReturnUserNotifications() {
        // Arrange
        List<NotificationResponseDTO> mockList = Arrays.asList(mockDto);
        when(notificationService.getNotificationsForUser(1L)).thenReturn(mockList);

        // Act
        ResponseEntity<List<NotificationResponseDTO>> response = notificationController.getUserNotifications(mockUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockList, response.getBody());
        verify(notificationService).getNotificationsForUser(1L);
    }

    @Test
    void getNotificationById_ShouldReturnNotification() {
        // Arrange
        when(notificationService.getNotificationById(100L)).thenReturn(mockDto);

        // Act
        ResponseEntity<NotificationResponseDTO> response = notificationController.getNotificationById(100L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockDto, response.getBody());
        verify(notificationService).getNotificationById(100L);
    }

    @Test
    void deleteNotification_ShouldReturnNoContent() {
        // Arrange
        doNothing().when(notificationService).deleteNotification(100L);

        // Act
        ResponseEntity<Void> response = notificationController.deleteNotification(100L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(notificationService).deleteNotification(100L);
    }

    @Test
    void getAllNotifications_ShouldAllowAdmin() {
        List<NotificationResponseDTO> mockList = Arrays.asList(mockDto);
        when(notificationService.getAllNotifications()).thenReturn(mockList);

        ResponseEntity<List<NotificationResponseDTO>> response = notificationController.getAllNotifications(mockAdmin);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockList, response.getBody());
        verify(notificationService).getAllNotifications();
    }

    @Test
    void getAllNotifications_ShouldDenyUser() {

        assertThrows(CustomException.class, () -> {
            notificationController.getAllNotifications(mockUser);
        });

        verify(notificationService, never()).getAllNotifications();
    }
}
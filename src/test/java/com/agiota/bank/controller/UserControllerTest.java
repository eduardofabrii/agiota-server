package com.agiota.bank.controller;

import com.agiota.bank.dto.request.UserRequestDTO;
import com.agiota.bank.dto.response.UserResponseDTO;
import com.agiota.bank.model.user.UserRole;
import com.agiota.bank.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserRequestDTO requestDTO;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new UserRequestDTO(
                "João Silva", "password123", "joao@test.com", UserRole.USER
        );
        responseDTO = new UserResponseDTO(
                1L, "João Silva", "joao@test.com", 
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void findById_shouldReturnUser() {
        // Given
        Long userId = 1L;
        when(userService.listUserById(userId)).thenReturn(responseDTO);

        // When
        ResponseEntity<UserResponseDTO> response = userController.listUserById(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(userService).listUserById(userId);
    }

    @Test
    void create_shouldCreateUser() throws Exception {
        // Given
        when(userService.create(any(UserRequestDTO.class))).thenReturn(responseDTO);

        // When
        ResponseEntity<UserResponseDTO> response = userController.create(requestDTO);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(userService).create(requestDTO);
    }

    @Test
    void update_shouldUpdateUser() {
        // Given
        Long userId = 1L;
        when(userService.update(eq(userId), any(UserRequestDTO.class))).thenReturn(responseDTO);

        // When
        ResponseEntity<UserResponseDTO> response = userController.updateUser(requestDTO, userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(userService).update(userId, requestDTO);
    }

    @Test
    void softDelete_shouldDeleteUser() {
        // Given
        Long userId = 1L;
        doNothing().when(userService).softDelete(userId);

        // When
        ResponseEntity<String> response = userController.softDelete(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).softDelete(userId);
    }

    @Test
    void restore_shouldRestoreUser() {
        // Given
        Long userId = 1L;
        doNothing().when(userService).restore(userId);

        // When
        ResponseEntity<String> response = userController.restore(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).restore(userId);
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        // Given
        List<UserResponseDTO> users = Arrays.asList(responseDTO);
        when(userService.listAll()).thenReturn(users);

        // When
        ResponseEntity<List<UserResponseDTO>> response = userController.listAll();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
        verify(userService).listAll();
    }
}
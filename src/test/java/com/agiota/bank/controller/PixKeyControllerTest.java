package com.agiota.bank.controller;

import com.agiota.bank.dto.request.PixKeyRequestDTO;
import com.agiota.bank.dto.response.PixKeyResponseDTO;
import com.agiota.bank.model.pixkey.PixKeyTypes;
import com.agiota.bank.model.user.User;
import com.agiota.bank.service.pixkey.PixKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PixKeyControllerTest {

    @Mock
    private PixKeyService pixKeyService;

    @InjectMocks
    private PixKeyController pixKeyController;

    private User mockUser;
    private PixKeyRequestDTO requestDTO;
    private PixKeyResponseDTO responseDTO;
    private String keyValue = "test@test.com";
    private Long accountId = 1L;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);

        requestDTO = new PixKeyRequestDTO(keyValue, PixKeyTypes.EMAIL);
        responseDTO = new PixKeyResponseDTO(keyValue, PixKeyTypes.EMAIL.name(), accountId);

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void createPixKey_shouldCallServiceAndReturnCreated() {
        // Arrange
        when(pixKeyService.createPixKey(any(PixKeyRequestDTO.class), eq(accountId), any(User.class))).thenReturn(responseDTO);

        // Act
        ResponseEntity<PixKeyResponseDTO> response = pixKeyController.createPixKey(requestDTO, accountId, mockUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(responseDTO);
        assertThat(response.getHeaders().getLocation().getPath()).endsWith("/" + keyValue);
        verify(pixKeyService).createPixKey(eq(requestDTO), eq(accountId), eq(mockUser));
    }

    @Test
    void getPixKey_shouldReturnKey() {
        // Arrange
        when(pixKeyService.getPixKey(keyValue)).thenReturn(responseDTO);

        // Act
        ResponseEntity<PixKeyResponseDTO> response = pixKeyController.getPixKey(keyValue);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDTO);
        verify(pixKeyService).getPixKey(keyValue);
    }

    @Test
    void listPixKeyByAccountId_shouldReturnKeys() {
        // Arrange
        List<PixKeyResponseDTO> keys = Collections.singletonList(responseDTO);
        when(pixKeyService.listPixKeyByAccountId(accountId)).thenReturn(keys);

        // Act
        ResponseEntity<List<PixKeyResponseDTO>> response = pixKeyController.listPixKeyByAccountId(accountId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(keys);
        verify(pixKeyService).listPixKeyByAccountId(accountId);
    }

    @Test
    void deletePixKey_shouldDeleteKey() {
        // Arrange
        doNothing().when(pixKeyService).deletePixKey(keyValue, mockUser);

        // Act
        ResponseEntity<Void> response = pixKeyController.deletePixKey(keyValue, mockUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(pixKeyService).deletePixKey(keyValue, mockUser);
    }
}

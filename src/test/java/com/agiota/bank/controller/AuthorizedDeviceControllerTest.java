package com.agiota.bank.controller;

import com.agiota.bank.dto.request.AuthorizedDeviceRequestDTO;
import com.agiota.bank.dto.response.AuthorizedDeviceResponseDTO;
import com.agiota.bank.model.device.DeviceType;
import com.agiota.bank.service.device.AuthorizedDeviceService;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizedDeviceControllerTest {

    @Mock
    private AuthorizedDeviceService deviceService;

    @InjectMocks
    private AuthorizedDeviceController deviceController;

    private AuthorizedDeviceRequestDTO mockRequestDTO;
    private AuthorizedDeviceResponseDTO mockResponseDTO;
    private final Long userId = 1L;
    private final Long deviceId = 1L;

    @BeforeEach
    void setUp() {
        mockRequestDTO = new AuthorizedDeviceRequestDTO(
                "Test Device",
                DeviceType.DESKTOP,
                "127.0.0.1",
                userId
        );

        mockResponseDTO = new AuthorizedDeviceResponseDTO(
                deviceId,
                "Test Device",
                DeviceType.DESKTOP,
                "127.0.0.1",
                LocalDateTime.now(),
                true,
                userId
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/v1/devices");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void authorizeDevice_shouldReturnCreated() {
        // Arrange
        when(deviceService.authorizeDevice(any(AuthorizedDeviceRequestDTO.class))).thenReturn(mockResponseDTO);

        // Act
        ResponseEntity<AuthorizedDeviceResponseDTO> response = deviceController.authorizeDevice(mockRequestDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockResponseDTO, response.getBody());
        assertEquals("/v1/devices/1", Objects.requireNonNull(response.getHeaders().getLocation()).getPath());
    }

    @Test
    void getDevicesByUserId_shouldReturnDeviceList() {
        // Arrange
        when(deviceService.getDevicesByUserId(userId)).thenReturn(Collections.singletonList(mockResponseDTO));

        // Act
        ResponseEntity<List<AuthorizedDeviceResponseDTO>> response = deviceController.getDevicesByUserId(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        assertEquals(mockResponseDTO, response.getBody().get(0));
    }

    @Test
    void getDeviceById_shouldReturnDevice() {
        // Arrange
        when(deviceService.getDeviceById(deviceId)).thenReturn(mockResponseDTO);

        // Act
        ResponseEntity<AuthorizedDeviceResponseDTO> response = deviceController.getDeviceById(deviceId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponseDTO, response.getBody());
    }

    @Test
    void revokeDevice_shouldReturnNoContent() {
        // Arrange
        doNothing().when(deviceService).revokeDevice(deviceId);

        // Act
        ResponseEntity<Void> response = deviceController.revokeDevice(deviceId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(deviceService, times(1)).revokeDevice(deviceId);
    }
}
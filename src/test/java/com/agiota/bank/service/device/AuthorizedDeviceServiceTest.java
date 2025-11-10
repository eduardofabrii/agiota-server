package com.agiota.bank.service.device;

import com.agiota.bank.dto.request.AuthorizedDeviceRequestDTO;
import com.agiota.bank.dto.response.AuthorizedDeviceResponseDTO;
import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.mapper.AuthorizedDeviceMapper;
import com.agiota.bank.model.device.AuthorizedDevice;
import com.agiota.bank.model.device.DeviceType;
import com.agiota.bank.model.user.User;
import com.agiota.bank.repository.AuthorizedDeviceRepository;
import com.agiota.bank.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizedDeviceServiceTest {

    @InjectMocks
    private AuthorizedDeviceServiceImpl deviceService;

    @Mock
    private AuthorizedDeviceRepository deviceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorizedDeviceMapper deviceMapper;

    private User mockUser;
    private AuthorizedDevice mockDevice;
    private AuthorizedDeviceRequestDTO mockRequestDTO;
    private AuthorizedDeviceResponseDTO mockResponseDTO;
    private final Long userId = 1L;
    private final Long deviceId = 1L;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(userId);

        mockRequestDTO = new AuthorizedDeviceRequestDTO(
                "Test Device",
                DeviceType.DESKTOP,
                "127.0.0.1",
                userId
        );

        mockDevice = new AuthorizedDevice(
                deviceId,
                "Test Device",
                DeviceType.DESKTOP,
                "127.0.0.1",
                LocalDateTime.now(),
                true,
                mockUser
        );

        mockResponseDTO = new AuthorizedDeviceResponseDTO(
                deviceId,
                "Test Device",
                DeviceType.DESKTOP,
                "127.0.0.1",
                mockDevice.getLastLoginDate(),
                true,
                userId
        );
    }

    @Test
    void authorizeDevice_shouldCreateAndReturnDevice() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(deviceMapper.toEntity(any(AuthorizedDeviceRequestDTO.class), any(User.class))).thenReturn(mockDevice);
        when(deviceRepository.save(any(AuthorizedDevice.class))).thenReturn(mockDevice);
        when(deviceMapper.toResponse(any(AuthorizedDevice.class))).thenReturn(mockResponseDTO);

        // Act
        AuthorizedDeviceResponseDTO result = deviceService.authorizeDevice(mockRequestDTO);

        // Assert
        assertThat(result).isEqualTo(mockResponseDTO);
        verify(userRepository).findById(userId);
        verify(deviceRepository).save(mockDevice);
    }

    @Test
    void authorizeDevice_shouldThrowException_whenUserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> deviceService.authorizeDevice(mockRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findById(userId);
        verify(deviceRepository, never()).save(any());
    }

    @Test
    void getDevicesByUserId_shouldReturnDeviceList() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(true);
        when(deviceRepository.findByUserId(userId)).thenReturn(Collections.singletonList(mockDevice));
        when(deviceMapper.toResponseList(anyList())).thenReturn(Collections.singletonList(mockResponseDTO));

        // Act
        List<AuthorizedDeviceResponseDTO> result = deviceService.getDevicesByUserId(userId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(mockResponseDTO);
        verify(userRepository).existsById(userId);
        verify(deviceRepository).findByUserId(userId);
    }

    @Test
    void getDevicesByUserId_shouldThrowException_whenUserNotFound() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> deviceService.getDevicesByUserId(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).existsById(userId);
        verify(deviceRepository, never()).findByUserId(any());
    }

    @Test
    void getDeviceById_shouldReturnDevice() {
        // Arrange
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(mockDevice));
        when(deviceMapper.toResponse(mockDevice)).thenReturn(mockResponseDTO);

        // Act
        AuthorizedDeviceResponseDTO result = deviceService.getDeviceById(deviceId);

        // Assert
        assertThat(result).isEqualTo(mockResponseDTO);
        verify(deviceRepository).findById(deviceId);
    }

    @Test
    void getDeviceById_shouldThrowException_whenDeviceNotFound() {
        // Arrange
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> deviceService.getDeviceById(deviceId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Device not found");

        verify(deviceRepository).findById(deviceId);
    }

    @Test
    void revokeDevice_shouldUpdateDeviceStatus() {
        // Arrange
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(mockDevice));

        // Act
        deviceService.revokeDevice(deviceId);

        // Assert
        verify(deviceRepository).findById(deviceId);
        verify(deviceRepository).save(mockDevice);
        assertThat(mockDevice.isAuthorized()).isFalse();
    }

    @Test
    void revokeDevice_shouldThrowException_whenDeviceNotFound() {
        // Arrange
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> deviceService.revokeDevice(deviceId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Device not found");

        verify(deviceRepository).findById(deviceId);
        verify(deviceRepository, never()).save(any());
    }
}
package com.agiota.bank.dto.response;

import com.agiota.bank.model.device.DeviceType;

import java.time.LocalDateTime;

public record AuthorizedDeviceResponseDTO(
        Long id,
        String deviceName,
        DeviceType deviceType,
        String ipAddress,
        LocalDateTime lastLoginDate,
        boolean isAuthorized,
        Long userId
) {}

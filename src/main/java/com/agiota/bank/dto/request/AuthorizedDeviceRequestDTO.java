package com.agiota.bank.dto.request;

import com.agiota.bank.model.device.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthorizedDeviceRequestDTO(
        @NotBlank String deviceName,
        @NotNull DeviceType deviceType,
        @NotBlank String ipAddress,
        @NotNull Long userId
) {}

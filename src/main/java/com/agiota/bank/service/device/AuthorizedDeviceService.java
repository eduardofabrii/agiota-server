package com.agiota.bank.service.device;

import com.agiota.bank.dto.request.AuthorizedDeviceRequestDTO;
import com.agiota.bank.dto.response.AuthorizedDeviceResponseDTO;

import java.util.List;

public interface AuthorizedDeviceService {
    AuthorizedDeviceResponseDTO authorizeDevice(AuthorizedDeviceRequestDTO dto);
    List<AuthorizedDeviceResponseDTO> getDevicesByUserId(Long userId);
    AuthorizedDeviceResponseDTO getDeviceById(Long id);
    void revokeDevice(Long id);
}

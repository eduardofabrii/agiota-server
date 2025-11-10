package com.agiota.bank.service.device;

import com.agiota.bank.dto.request.AuthorizedDeviceRequestDTO;
import com.agiota.bank.dto.response.AuthorizedDeviceResponseDTO;
import com.agiota.bank.exception.DeviceException;
import com.agiota.bank.exception.ErrorCode;
import com.agiota.bank.mapper.AuthorizedDeviceMapper;
import com.agiota.bank.model.device.AuthorizedDevice;
import com.agiota.bank.model.user.User;
import com.agiota.bank.repository.AuthorizedDeviceRepository;
import com.agiota.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorizedDeviceServiceImpl implements AuthorizedDeviceService {

    private final AuthorizedDeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final AuthorizedDeviceMapper deviceMapper;

    @Override
    public AuthorizedDeviceResponseDTO authorizeDevice(AuthorizedDeviceRequestDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new DeviceException(ErrorCode.USER_NOT_FOUND));

        deviceRepository.findByUserIdAndIpAddress(dto.userId(), dto.ipAddress()).ifPresent(device -> {
            throw new DeviceException(ErrorCode.DEVICE_ALREADY_AUTHORIZED);
        });

        AuthorizedDevice device = deviceMapper.toEntity(dto, user);
        AuthorizedDevice savedDevice = deviceRepository.save(device);

        return deviceMapper.toResponse(savedDevice);
    }

    @Override
    public List<AuthorizedDeviceResponseDTO> getDevicesByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DeviceException(ErrorCode.USER_NOT_FOUND);
        }
        List<AuthorizedDevice> devices = deviceRepository.findByUserId(userId);
        return deviceMapper.toResponseList(devices);
    }

    @Override
    public AuthorizedDeviceResponseDTO getDeviceById(Long id) {
        AuthorizedDevice device = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceException(ErrorCode.DEVICE_NOT_FOUND));
        return deviceMapper.toResponse(device);
    }

    @Override
    public void revokeDevice(Long id) {
        AuthorizedDevice device = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceException(ErrorCode.DEVICE_NOT_FOUND));

        device.setAuthorized(false);
        deviceRepository.save(device);
    }
}

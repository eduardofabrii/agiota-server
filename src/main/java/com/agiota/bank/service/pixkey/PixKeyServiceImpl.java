package com.agiota.bank.service.pixkey;

import com.agiota.bank.dto.request.PixKeyRequestDTO;
import com.agiota.bank.dto.response.PixKeyResponseDTO;
import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.mapper.PixKeyMapper;
import com.agiota.bank.model.pixkey.PixKey;
import com.agiota.bank.repository.PixKeyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class PixKeyServiceImpl implements PixKeyService {
    private final PixKeyRepository pixKeyRepository;
    private final PixKeyMapper mapper;

    @Override
    public PixKeyResponseDTO createPixKey(PixKeyRequestDTO dto, Long ownerId) {
        PixKey pixKey = mapper.toPixKeyPostRequest(dto, ownerId);
        pixKeyRepository.save(pixKey);
        return mapper.toPixKeyPostResponse(pixKey);
    }

    @Override
    public PixKeyResponseDTO getPixKey(String keyValue) {
        PixKey pixKey = pixKeyRepository.findByKeyValue(keyValue)
                .orElseThrow(() -> new ResourceNotFoundException("PixKey not found"));
        return mapper.toPixKeyPostResponse(pixKey);
    }

    @Override
    public List<PixKeyResponseDTO> listPixKeyByOwnerId(Long ownerId) {
        List<PixKey> pixKeys = pixKeyRepository.findByOwnerId(ownerId);
        return mapper.toPixKeyListResponse(pixKeys);
    }

    @Override
    public PixKeyResponseDTO updatePixKey(String keyValue, PixKeyRequestDTO dto) {
        PixKey pixKey = pixKeyRepository.findByKeyValue(keyValue)
                .orElseThrow(() -> new ResourceNotFoundException("PixKey not found"));
        pixKeyRepository.save(pixKey);
        return mapper.toPixKeyPostResponse(pixKey);
    }

    @Override
    public void deletePixKey(String keyValue) {
        if (!pixKeyRepository.existsById(keyValue)) {
            throw new ResourceNotFoundException("PixKey not found");
        }
        pixKeyRepository.deleteById(keyValue);
    }
}

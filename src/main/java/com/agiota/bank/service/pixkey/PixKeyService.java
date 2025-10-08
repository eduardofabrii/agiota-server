package com.agiota.bank.service.pixkey;

import com.agiota.bank.dto.request.PixKeyRequestDTO;
import com.agiota.bank.dto.response.PixKeyResponseDTO;

import java.util.List;

public interface PixKeyService {
    public PixKeyResponseDTO createPixKey(PixKeyRequestDTO dto, Long ownerId);
    public PixKeyResponseDTO getPixKey(String keyValue);
    public List<PixKeyResponseDTO> listPixKeyByOwnerId(Long ownerId);
    public PixKeyResponseDTO updatePixKey(String keyValue, PixKeyRequestDTO dto);
    public void deletePixKey(String keyValue);
}

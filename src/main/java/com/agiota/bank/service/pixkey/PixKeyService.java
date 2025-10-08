package com.agiota.bank.service.pixkey;

import com.agiota.bank.dto.request.PixKeyRequestDTO;
import com.agiota.bank.dto.response.PixKeyResponseDTO;

import java.util.List;

public interface PixKeyService {
    PixKeyResponseDTO createPixKey(PixKeyRequestDTO dto, Long accountId);

    PixKeyResponseDTO getPixKey(String keyValue);

    List<PixKeyResponseDTO> listPixKeyByAccountId(Long accountId);

    void deletePixKey(String keyValue);
}

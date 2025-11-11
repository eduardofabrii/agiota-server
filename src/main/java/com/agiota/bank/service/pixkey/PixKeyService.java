package com.agiota.bank.service.pixkey;

import com.agiota.bank.dto.request.PixKeyRequestDTO;
import com.agiota.bank.dto.response.PixKeyResponseDTO;
import com.agiota.bank.model.user.User;

import java.util.List;

public interface PixKeyService {
    PixKeyResponseDTO createPixKey(PixKeyRequestDTO dto, Long accountId, User user);
    PixKeyResponseDTO getPixKey(String keyValue);
    List<PixKeyResponseDTO> listPixKeyByAccountId(Long accountId); // Revertido
    void deletePixKey(String keyValue, User user);
}

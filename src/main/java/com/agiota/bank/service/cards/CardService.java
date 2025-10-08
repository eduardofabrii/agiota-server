package com.agiota.bank.service.cards;

import com.agiota.bank.dto.request.CardRequestDTO;
import com.agiota.bank.dto.response.CardResponseDTO;

import java.util.List;

public interface CardService {
    CardResponseDTO create(CardRequestDTO dto);
    CardResponseDTO getById(Long id);
    List<CardResponseDTO> getAll();
    CardResponseDTO update(Long id, CardRequestDTO dto);
    void delete(Long id);
}

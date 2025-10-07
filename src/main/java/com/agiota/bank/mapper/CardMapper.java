package com.agiota.bank.mapper;

import com.agiota.bank.model.cards.Cards;
import com.agiota.bank.dto.request.CardRequestDTO;
import com.agiota.bank.dto.response.CardResponseDTO;

public interface CardMapper {
    public static Cards toEntity(CardRequestDTO dto) {
        Cards card = new Cards();
        card.setNumber(dto.getNumber());
        card.setHolderName(dto.getHolderName());
        card.setExpirationDate(dto.getExpirationDate());
        card.setCvv(dto.getCvv());
        return card;
    }

    public static CardResponseDTO toResponseDTO(Cards card) {
        CardResponseDTO dto = new CardResponseDTO();
        dto.setId(card.getId());
        dto.setNumber(card.getNumber());
        dto.setHolderName(card.getHolderName());
        dto.setExpirationDate(card.getExpirationDate());
        return dto;
    }
}

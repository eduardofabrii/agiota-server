package com.agiota.bank.mapper;

import java.util.List;

import com.agiota.bank.model.cards.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.agiota.bank.dto.request.CardRequestDTO;
import com.agiota.bank.dto.response.CardResponseDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account.id", source = "accountId")
    Card toCardPostRequest(CardRequestDTO cardRequest);

    @Mapping(target = "accountId", source = "account.id")
    CardResponseDTO toCardPostResponse(Card card);

    List<CardResponseDTO> toCardListResponse(List<Card> cards);
}

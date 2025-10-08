package com.agiota.bank.service.cards;

import com.agiota.bank.dto.request.CardRequestDTO;
import com.agiota.bank.dto.response.CardResponseDTO;
import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.mapper.CardMapper;
import com.agiota.bank.model.cards.Card;
import com.agiota.bank.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @Override
    public CardResponseDTO create(CardRequestDTO dto) {
        Card card = cardMapper.toCardPostRequest(dto);
        card = cardRepository.save(card);
        return cardMapper.toCardPostResponse(card);
    }

    @Override
    public CardResponseDTO getById(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        return cardMapper.toCardPostResponse(card);
    }

    @Override
    public List<CardResponseDTO> getAll() {
        return cardMapper.toCardListResponse(cardRepository.findAll());
    }

    @Override
    public CardResponseDTO update(Long id, CardRequestDTO dto) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        card.setNumber(dto.number());
        card.setHolderName(dto.holderName());
        card.setExpirationDate(dto.expirationDate());
        card.setCvv(dto.cvv());
        card = cardRepository.save(card);
        return cardMapper.toCardPostResponse(card);
    }

    @Override
    public void delete(Long id) {
        cardRepository.deleteById(id);
    }
}

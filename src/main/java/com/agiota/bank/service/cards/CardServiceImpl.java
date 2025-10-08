package com.agiota.bank.service.cards;

import com.agiota.bank.dto.request.CardRequestDTO;
import com.agiota.bank.dto.response.CardResponseDTO;
import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.mapper.CardMapper;
import com.agiota.bank.model.cards.Cards;
import com.agiota.bank.repository.CardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    public CardServiceImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Override
    public CardResponseDTO create(CardRequestDTO dto) {
        Cards card = CardMapper.toEntity(dto);
        card = cardRepository.save(card);
        return CardMapper.toResponseDTO(card);
    }

    @Override
    public CardResponseDTO getById(Long id) {
        Cards card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        return CardMapper.toResponseDTO(card);
    }

    @Override
    public List<CardResponseDTO> getAll() {
        return cardRepository.findAll().stream()
                .map(CardMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CardResponseDTO update(Long id, CardRequestDTO dto) {
        Cards card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        card.setNumber(dto.getNumber());
        card.setHolderName(dto.getHolderName());
        card.setExpirationDate(dto.getExpirationDate());
        card.setCvv(dto.getCvv());
        card = cardRepository.save(card);
        return CardMapper.toResponseDTO(card);
    }

    @Override
    public void delete(Long id) {
        cardRepository.deleteById(id);
    }
}

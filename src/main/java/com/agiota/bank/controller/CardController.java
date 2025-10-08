package com.agiota.bank.controller;

import com.agiota.bank.dto.request.CardRequestDTO;
import com.agiota.bank.dto.response.CardResponseDTO;
import com.agiota.bank.service.cards.CardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public CardResponseDTO create(@RequestBody CardRequestDTO dto) {
        return cardService.create(dto);
    }

    @GetMapping("/{id}")
    public CardResponseDTO getById(@PathVariable Long id) {
        return cardService.getById(id);
    }

    @GetMapping
    public List<CardResponseDTO> getAll() {
        return cardService.getAll();
    }

    @PutMapping("/{id}")
    public CardResponseDTO update(@PathVariable Long id, @RequestBody CardRequestDTO dto) {
        return cardService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        cardService.delete(id);
    }
}

package com.agiota.bank.controller;

import com.agiota.bank.dto.request.CardRequestDTO;
import com.agiota.bank.dto.response.CardResponseDTO;
import com.agiota.bank.service.cards.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/cards")
@RequiredArgsConstructor
@Tag(name = "Cartões", description = "Operações relacionadas aos cartões bancários")
public class CardController {

    private final CardService cardService;

    @PostMapping
    @Operation(summary = "Criar novo cartão")
    public ResponseEntity<CardResponseDTO> create(@Valid @RequestBody CardRequestDTO dto) {
        CardResponseDTO response = cardService.create(dto);
        
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
                
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cartão por ID")
    public ResponseEntity<CardResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getById(id));
    }

    @GetMapping
    @Operation(summary = "Listar todos os cartões")
    public ResponseEntity<List<CardResponseDTO>> getAll() {
        return ResponseEntity.ok(cardService.getAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cartão")
    public ResponseEntity<CardResponseDTO> update(@PathVariable Long id, @Valid @RequestBody CardRequestDTO dto) {
        return ResponseEntity.ok(cardService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir cartão")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

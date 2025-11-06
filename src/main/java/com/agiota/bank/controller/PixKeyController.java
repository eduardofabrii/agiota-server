package com.agiota.bank.controller;

import com.agiota.bank.dto.request.PixKeyRequestDTO;
import com.agiota.bank.dto.response.PixKeyResponseDTO;
import com.agiota.bank.service.pixkey.PixKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/pix-keys")
@Tag(name = "Chaves PIX", description = "Operações relacionadas às chaves PIX")
public class PixKeyController {
    private PixKeyService pixKeyService;

    @PostMapping("/{accountId}")
    @Operation(summary = "Criar nova chave PIX")
    public ResponseEntity<PixKeyResponseDTO> createPixKey(
            @Valid @RequestBody PixKeyRequestDTO dto,
            @PathVariable Long accountId) {
        PixKeyResponseDTO response = pixKeyService.createPixKey(dto, accountId);
        
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{keyValue}")
                .buildAndExpand(response.keyValue())
                .toUri();
                
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/{keyValue}")
    @Operation(summary = "Buscar chave PIX")
    public ResponseEntity<PixKeyResponseDTO> getPixKey(@PathVariable String keyValue) {
        PixKeyResponseDTO response = pixKeyService.getPixKey(keyValue);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Listar chaves PIX por conta")
    public ResponseEntity<List<PixKeyResponseDTO>> listPixKeyByAccountId(@PathVariable Long accountId) {
        List<PixKeyResponseDTO> response = pixKeyService.listPixKeyByAccountId(accountId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{keyValue}")
    @Operation(summary = "Excluir chave PIX")
    public ResponseEntity<Void> deletePixKey(@PathVariable String keyValue) {
        pixKeyService.deletePixKey(keyValue);
        return ResponseEntity.noContent().build();
    }
}

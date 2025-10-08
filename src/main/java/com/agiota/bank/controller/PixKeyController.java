package com.agiota.bank.controller;

import com.agiota.bank.dto.request.PixKeyRequestDTO;
import com.agiota.bank.dto.response.PixKeyResponseDTO;
import com.agiota.bank.service.pixkey.PixKeyService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/pix-keys")
public class PixKeyController {
    private PixKeyService pixKeyService;

    @PostMapping("/{accountId}")
    public ResponseEntity<PixKeyResponseDTO> createPixKey(
            @RequestBody PixKeyRequestDTO dto,
            @PathVariable Long accountId) {
        PixKeyResponseDTO response = pixKeyService.createPixKey(dto, accountId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{keyValue}")
    public ResponseEntity<PixKeyResponseDTO> getPixKey(@PathVariable String keyValue) {
        PixKeyResponseDTO response = pixKeyService.getPixKey(keyValue);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<PixKeyResponseDTO>> listPixKeyByAccountId(@PathVariable Long accountId) {
        List<PixKeyResponseDTO> response = pixKeyService.listPixKeyByAccountId(accountId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{keyValue}")
    public ResponseEntity<Void> deletePixKey(@PathVariable String keyValue) {
        pixKeyService.deletePixKey(keyValue);
        return ResponseEntity.noContent().build();
    }
}

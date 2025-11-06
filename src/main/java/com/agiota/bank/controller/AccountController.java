package com.agiota.bank.controller;

import com.agiota.bank.dto.request.AccountRequestDTO;
import com.agiota.bank.dto.response.AccountResponseDTO;
import com.agiota.bank.service.account.AccountService;
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
@RequestMapping("/v1/account")
@RequiredArgsConstructor
@Tag(name = "Contas", description = "Operações relacionadas às contas bancárias")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Criar nova conta")
    public ResponseEntity<AccountResponseDTO> create(@Valid @RequestBody AccountRequestDTO requestDTO) {
        AccountResponseDTO response = accountService.create(requestDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar conta por ID")
    public ResponseEntity<AccountResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.findById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Buscar contas por usuário")
    public ResponseEntity<List<AccountResponseDTO>> findByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.findByUserId(userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar conta")
    public ResponseEntity<AccountResponseDTO> update(@PathVariable Long id, @Valid @RequestBody AccountRequestDTO requestDTO) {
        AccountResponseDTO response = accountService.update(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir conta")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
package com.agiota.bank.controller;

import com.agiota.bank.dto.request.BankStatementRequestDTO;
import com.agiota.bank.dto.response.BankStatementResponseDTO;
import com.agiota.bank.model.statement.StatementStatus;
import com.agiota.bank.model.user.User;
import com.agiota.bank.service.statement.BankStatementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/statements")
@RequiredArgsConstructor
@Tag(name = "Extratos Bancários", description = "Operações relacionadas aos extratos bancários")
public class BankStatementController {
    private final BankStatementService bankStatementService;

    @PostMapping
    @Operation(summary = "Gerar novo extrato bancário")
    public ResponseEntity<BankStatementResponseDTO> generate(
            @Valid @RequestBody BankStatementRequestDTO dto,
            @AuthenticationPrincipal User user) {
        BankStatementResponseDTO response = bankStatementService.generate(dto, user);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar extrato por ID")
    public ResponseEntity<BankStatementResponseDTO> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bankStatementService.getById(id, user));
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Listar extratos por conta")
    public ResponseEntity<List<BankStatementResponseDTO>> getByAccountId(
            @PathVariable Long accountId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bankStatementService.getByAccountId(accountId, user));
    }

    @GetMapping("/account/{accountId}/status/{status}")
    @Operation(summary = "Listar extratos por conta e status")
    public ResponseEntity<List<BankStatementResponseDTO>> getByAccountIdAndStatus(
            @PathVariable Long accountId,
            @PathVariable StatementStatus status,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bankStatementService.getByAccountIdAndStatus(accountId, status, user));
    }

    @PatchMapping("/{id}/status/{status}")
    @Operation(summary = "Atualizar status do extrato")
    public ResponseEntity<BankStatementResponseDTO> updateStatus(
            @PathVariable Long id,
            @PathVariable StatementStatus status,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bankStatementService.updateStatus(id, status, user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir extrato")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        bankStatementService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}


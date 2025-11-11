package com.agiota.bank.controller;

import com.agiota.bank.dto.request.TransactionRequestDTO;
import com.agiota.bank.dto.response.TransactionResponseDTO;
import com.agiota.bank.model.user.User;
import com.agiota.bank.service.transaction.TransactionService;
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
@RequestMapping("/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transações", description = "Operações relacionadas às transações financeiras")
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/{id}")
    @Operation(summary = "Buscar transação por ID")
    public ResponseEntity<TransactionResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.listTransactionById(id));
    }

    @GetMapping("/sent/{id}")
    @Operation(summary = "Listar transações enviadas")
    public ResponseEntity<List<TransactionResponseDTO>> listUserTransactionsSent(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.listAccountTransactionsSent(id));
    }

    @GetMapping("/received/{id}")
    @Operation(summary = "Listar transações recebidas")
    public ResponseEntity<List<TransactionResponseDTO>> listUserTransactionsReceived(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.listAccountTransactionsReceived(id));
    }

    @GetMapping("/all")
    @Operation(summary = "Listar todas as transações")
    public ResponseEntity<List<TransactionResponseDTO>> listAllTransactions() {
        return ResponseEntity.ok(transactionService.listAllTransactions());
    }

    @PostMapping
    @Operation(summary = "Criar nova transação")
    public ResponseEntity<TransactionResponseDTO> create(@Valid @RequestBody TransactionRequestDTO dto, @AuthenticationPrincipal User user) {
        TransactionResponseDTO response = transactionService.create(dto, user);
        
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
                
        return ResponseEntity.created(uri).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir transação")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar transação")
    public ResponseEntity<TransactionResponseDTO> update(@Valid @RequestBody TransactionRequestDTO dto, @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.update(id, dto));
    }
}

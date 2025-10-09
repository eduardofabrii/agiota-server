package com.agiota.bank.controller;

import com.agiota.bank.dto.request.TransactionRequestDTO;
import com.agiota.bank.dto.response.TransactionResponseDTO;
import com.agiota.bank.service.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.listTransactionById(id));
    }
    @GetMapping("/sent/{id}")
    public ResponseEntity<List<TransactionResponseDTO>> listUserTransactionsSent(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.listAccountTransactionsSent(id));
    }
    @GetMapping("/received/{id}")
    public ResponseEntity<List<TransactionResponseDTO>> listUserTransactionsReceived(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.listAccountTransactionsReceived(id));
    }
    @PostMapping("/{originAccountId}")
    public ResponseEntity<TransactionResponseDTO> create(@RequestBody TransactionRequestDTO dto, @PathVariable Long originAccountId) {
        return ResponseEntity.ok(transactionService.create(dto,originAccountId));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> update(@RequestBody TransactionRequestDTO dto, @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.update(id, dto));
    }


}

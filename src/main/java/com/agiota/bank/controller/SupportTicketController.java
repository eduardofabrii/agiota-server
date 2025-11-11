package com.agiota.bank.controller;

import com.agiota.bank.dto.request.SupportTicketRequestDTO;
import com.agiota.bank.dto.request.UpdateSupportTicketRequestDTO;
import com.agiota.bank.dto.response.SupportTicketResponseDTO;
import com.agiota.bank.model.user.User;
import com.agiota.bank.service.supportticket.SupportTicketService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/support-tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets de Suporte", description = "Operações relacionadas aos tickets de suporte")
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    @PostMapping
    public ResponseEntity<SupportTicketResponseDTO> create(@Valid @RequestBody SupportTicketRequestDTO request, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supportTicketService.create(request, user));
    }

    @GetMapping
    public ResponseEntity<List<SupportTicketResponseDTO>> findAllByUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(supportTicketService.findAllByUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupportTicketResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(supportTicketService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupportTicketResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UpdateSupportTicketRequestDTO request) {
        return ResponseEntity.ok(supportTicketService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        supportTicketService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

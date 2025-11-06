package com.agiota.bank.controller;

import com.agiota.bank.dto.request.BeneficiaryRequestDTO;
import com.agiota.bank.dto.response.BeneficiaryResponseDTO;
import com.agiota.bank.service.beneficiary.BeneficiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/beneficiaries")
@Tag(name = "Beneficiários", description = "Operações relacionadas aos beneficiários")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    @GetMapping("/account/{ownerAccountId}")
    @Operation(summary = "Listar todos os beneficiários de uma conta")
    public ResponseEntity<List<BeneficiaryResponseDTO>> listByOwnerAccount(
            @PathVariable Long ownerAccountId
    ) {
        return ResponseEntity.ok(beneficiaryService.listByOwnerAccount(ownerAccountId));
    }

    @GetMapping("/{id}/account/{ownerAccountId}")
    @Operation(summary = "Buscar beneficiário por ID")
    public ResponseEntity<BeneficiaryResponseDTO> findById(
            @PathVariable Long id,
            @PathVariable Long ownerAccountId
    ) {
        return ResponseEntity.ok(beneficiaryService.findById(id, ownerAccountId));
    }

    @PostMapping("/{ownerAccountId}")
    @Operation(summary = "Criar novo beneficiário")
    public ResponseEntity<BeneficiaryResponseDTO> create(
            @PathVariable Long ownerAccountId,
            @Valid @RequestBody BeneficiaryRequestDTO requestDTO
    ) throws URISyntaxException {
        BeneficiaryResponseDTO response = beneficiaryService.create(ownerAccountId, requestDTO);
        return ResponseEntity.created(new URI("/v1/beneficiaries/" + response.id() + "/account/" + ownerAccountId))
                .body(response);
    }

    @PutMapping("/{id}/account/{ownerAccountId}")
    @Operation(summary = "Atualizar beneficiário")
    public ResponseEntity<BeneficiaryResponseDTO> update(
            @PathVariable Long id,
            @PathVariable Long ownerAccountId,
            @Valid @RequestBody BeneficiaryRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(beneficiaryService.update(id, ownerAccountId, requestDTO));
    }

    @DeleteMapping("/{id}/account/{ownerAccountId}")
    @Operation(summary = "Excluir beneficiário")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @PathVariable Long ownerAccountId
    ) {
        beneficiaryService.delete(id, ownerAccountId);
        return ResponseEntity.noContent().build();
    }
}
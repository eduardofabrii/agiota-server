package com.agiota.bank.controller;

import com.agiota.bank.dto.request.AccountRequestDTO;
import com.agiota.bank.dto.request.UpdateAccountStatusDTO;
import com.agiota.bank.dto.response.AccountResponseDTO;
import com.agiota.bank.service.account.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Contas", description = "Operações relacionadas às contas bancárias")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Criar nova conta", description = "Cria uma nova conta bancária para um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conta criada com sucesso",
                    content = @Content(schema = @Schema(implementation = AccountResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conta já existe")
    })
    public ResponseEntity<AccountResponseDTO> create(@Valid @RequestBody AccountRequestDTO requestDTO) {
        AccountResponseDTO response = accountService.create(requestDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar conta por ID", description = "Retorna os detalhes de uma conta específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta encontrada",
                    content = @Content(schema = @Schema(implementation = AccountResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    public ResponseEntity<AccountResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.findById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Buscar contas por usuário", description = "Retorna todas as contas de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contas encontradas"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<List<AccountResponseDTO>> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.findByUserId(userId));
    }

    @GetMapping
    @Operation(summary = "Listar todas as contas", description = "Retorna todas as contas do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contas listadas com sucesso")
    })
    public ResponseEntity<List<AccountResponseDTO>> findAll() {
        return ResponseEntity.ok(accountService.findAll());
    }


    @PutMapping("/{id}")
    @Operation(summary = "Atualizar conta", description = "Atualiza os dados de uma conta existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = AccountResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    public ResponseEntity<AccountResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody AccountRequestDTO requestDTO) {
        AccountResponseDTO response = accountService.update(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status da conta", description = "Atualiza apenas o status de uma conta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status inválido"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    public ResponseEntity<AccountResponseDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAccountStatusDTO statusDTO) {
        AccountResponseDTO response = accountService.updateStatus(id, statusDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir conta", description = "Exclui uma conta bancária (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Conta excluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Não é possível excluir conta com saldo"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/balance")
    @Operation(summary = "Consultar saldo", description = "Retorna o saldo atual de uma conta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo consultado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    public ResponseEntity<AccountResponseDTO> getBalance(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.findById(id));
    }
}
package com.agiota.bank.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.agiota.bank.dto.request.UserRequestDTO;
import com.agiota.bank.dto.response.UserResponseDTO;
import com.agiota.bank.service.user.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/user")
@Tag(name = "Usuários", description = "Operações relacionadas aos usuários")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Listar todos os usuários")
    public ResponseEntity<List<UserResponseDTO>> listAll() {
        return ResponseEntity.ok(userService.listAll());
    }

    @GetMapping("{id}")
    @Operation(summary = "Buscar usuário por ID")
    public ResponseEntity<UserResponseDTO> listUserById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(userService.listUserById(id));
    }

    @PutMapping("{id}")
    @Operation(summary = "Atualizar usuário")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Valid @RequestBody UserRequestDTO dto,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    @PostMapping
    @Operation(summary = "Criar novo usuário")
    public ResponseEntity<UserResponseDTO> create(
            @Valid @RequestBody UserRequestDTO postRequest
    ) throws URISyntaxException {
        UserResponseDTO response = userService.create(postRequest);
        return ResponseEntity.created(new URI("/v1/user/" + response.id())).body(response);
    }

    @DeleteMapping("deactivate/{id}")
    @Operation(summary = "Desativar usuário")
    public ResponseEntity<String> softDelete(@PathVariable Long id) {
        userService.softDelete(id);
        return ResponseEntity.ok("User soft deleted successfully");
    }

    @PutMapping("restore/{id}")
    @Operation(summary = "Restaurar usuário")
    public ResponseEntity<String> restore(
            @PathVariable Long id) {
        userService.restore(id);
        return ResponseEntity.ok("User restored successfully");
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Excluir usuário")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

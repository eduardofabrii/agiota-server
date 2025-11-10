package com.agiota.bank.controller;

import com.agiota.bank.dto.request.AuthorizedDeviceRequestDTO;
import com.agiota.bank.dto.response.AuthorizedDeviceResponseDTO;
import com.agiota.bank.service.device.AuthorizedDeviceService;
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
@RequestMapping("/v1/devices")
@RequiredArgsConstructor
@Tag(name = "Dispositivos Autorizados", description = "Operações para gerenciar dispositivos autorizados dos usuários")
public class AuthorizedDeviceController {

    private final AuthorizedDeviceService deviceService;

    @PostMapping
    @Operation(summary = "Autorizar um novo dispositivo")
    public ResponseEntity<AuthorizedDeviceResponseDTO> authorizeDevice(@Valid @RequestBody AuthorizedDeviceRequestDTO dto) {
        AuthorizedDeviceResponseDTO response = deviceService.authorizeDevice(dto);
        
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
                
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar todos os dispositivos de um usuário")
    public ResponseEntity<List<AuthorizedDeviceResponseDTO>> getDevicesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(deviceService.getDevicesByUserId(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um dispositivo por ID")
    public ResponseEntity<AuthorizedDeviceResponseDTO> getDeviceById(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.getDeviceById(id));
    }

    @PatchMapping("/{id}/revoke")
    @Operation(summary = "Revogar o acesso de um dispositivo")
    public ResponseEntity<Void> revokeDevice(@PathVariable Long id) {
        deviceService.revokeDevice(id);
        return ResponseEntity.noContent().build();
    }
}

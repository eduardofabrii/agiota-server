package com.agiota.bank.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.agiota.bank.dto.request.UserRequestDTO;
import com.agiota.bank.dto.response.UserResponseDTO;
import com.agiota.bank.service.user.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> listAll() {
        return ResponseEntity.ok(userService.listAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<UserResponseDTO> listUserById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(userService.listUserById(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Valid @RequestBody UserRequestDTO dto,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(
            @Valid @RequestBody UserRequestDTO postRequest
    ) throws URISyntaxException {
        UserResponseDTO response = userService.create(postRequest);
        return ResponseEntity.created(new URI("/v1/user/" + response.id())).body(response);
    }

    @DeleteMapping("deactivate/{id}")
    public ResponseEntity<String> softDelete(@PathVariable Long id) {
        userService.softDelete(id);
        return ResponseEntity.ok("User soft deleted successfully");
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

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

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO postRequest) throws URISyntaxException {
        UserResponseDTO response = userService.create(postRequest);
        return ResponseEntity.created(new URI("/v1/user/" + response.id())).body(response);
    }
}

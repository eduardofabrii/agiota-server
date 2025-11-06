package com.agiota.bank.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agiota.bank.dto.request.AuthenticationRequestDTO;
import com.agiota.bank.dto.response.LoginResponseDTO;
import com.agiota.bank.infra.token.TokenConfig;
import com.agiota.bank.model.user.User;
import com.agiota.bank.service.user.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Operações relacionadas à autenticação")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenConfig tokenConfig;

    @PostMapping("/login")
    @Operation(summary = "Fazer login no sistema")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationRequestDTO dto) {
        var userPassword = new UsernamePasswordAuthenticationToken(dto.name(), dto.password());
        var auth = this.authenticationManager.authenticate(userPassword);

        var token = tokenConfig.generateToken((User) auth.getPrincipal());

        userService.updateLastLogin(dto.name());
        
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

}
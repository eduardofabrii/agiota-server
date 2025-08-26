package com.agiota.bank.controller;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.agiota.bank.dto.request.UserRequestDTO;
import com.agiota.bank.dto.response.UserResponseDTO;
import com.agiota.bank.service.user.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RequestMapping("v1/user")
public class UserController {

    private UserService userService;

    public ResponseEntity<UserResponseDTO> saveUser(@Valid @RequestBody UserRequestDTO postRequest) throws URISyntaxException {
        UserResponseDTO response = userService.create(postRequest);
        return ResponseEntity.created(new URI("/v1/user/" + response.id())).body(response);
    }
}

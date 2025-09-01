package com.agiota.bank.dto.request;

import com.agiota.bank.model.user.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(Integer id,
                             String name,
                             String password,
                             @Email String email,
                             @NotBlank UserRole role) {
}

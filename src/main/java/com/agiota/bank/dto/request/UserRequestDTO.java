package com.agiota.bank.dto.request;

import com.agiota.bank.model.user.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserRequestDTO(String name,
                             String password,
                             @Email String email,
                             @NotNull UserRole role) {
}

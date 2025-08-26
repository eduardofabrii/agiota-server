package com.agiota.bank.dto.request;


import com.agiota.bank.model.user.UserRole;

public record UserRequestDTO(Integer id,
                             String name,
                             String password,
                             String email,
                             UserRole role) {
}

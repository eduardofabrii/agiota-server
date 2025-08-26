package com.agiota.bank.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record UserResponseDTO(Long id,
                              String name,
                              String email,
                              @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") LocalDateTime createdAt,
                              @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") LocalDateTime lastLogin) {
}
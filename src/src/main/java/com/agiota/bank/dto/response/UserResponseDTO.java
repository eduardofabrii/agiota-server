package com.agiota.bank.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record UserResponseDTO(Long id,
                              String nome,
                              String email,
                              @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") LocalDateTime dataCriacao,
                              @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") LocalDateTime ultimoLogin,
                              String perfil,
                              boolean ativo) {
}
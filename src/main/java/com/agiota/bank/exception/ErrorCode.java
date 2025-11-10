package com.agiota.bank.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // Card Errors
    CARD_NOT_FOUND("CARD-001", "Cartão não encontrado."),
    INVALID_CARD_DATA("CARD-002", "Os dados do cartão são inválidos."),
    CARD_ALREADY_EXISTS("CARD-003", "Este cartão já está cadastrado."),

    // Device Errors
    DEVICE_NOT_FOUND("DEVICE-001", "Dispositivo não encontrado."),
    DEVICE_ALREADY_AUTHORIZED("DEVICE-002", "Este dispositivo já foi autorizado."),

    // User Errors
    USER_NOT_FOUND("USER-001", "Usuário não encontrado.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

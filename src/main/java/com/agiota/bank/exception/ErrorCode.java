package com.agiota.bank.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // Account Errors
    ACCOUNT_NOT_FOUND("ACCOUNT-001", "Conta não encontrada."),
    ACCOUNT_ALREADY_EXISTS("ACCOUNT-002", "Esta conta já existe."),
    ACCOUNT_INACTIVE("ACCOUNT-003", "Conta inativa."),
    ACCOUNT_SUSPENDED("ACCOUNT-004", "Conta suspensa."),
    ACCOUNT_CLOSED("ACCOUNT-005", "Conta encerrada."),
    INSUFFICIENT_BALANCE("ACCOUNT-006", "Saldo insuficiente."),
    INVALID_ACCOUNT_TYPE("ACCOUNT-007", "Tipo de conta inválido."),
    ACCOUNT_NUMBER_ALREADY_EXISTS("ACCOUNT-008", "Número de conta já existe."),
    CANNOT_DELETE_ACCOUNT_WITH_BALANCE("ACCOUNT-009", "Não é possível excluir conta com saldo."),
    
    // Card Errors
    CARD_NOT_FOUND("CARD-001", "Cartão não encontrado."),
    INVALID_CARD_DATA("CARD-002", "Os dados do cartão são inválidos."),
    CARD_ALREADY_EXISTS("CARD-003", "Este cartão já está cadastrado."),

    // Device Errors
    DEVICE_NOT_FOUND("DEVICE-001", "Dispositivo não encontrado."),
    DEVICE_ALREADY_AUTHORIZED("DEVICE-002", "Este dispositivo já foi autorizado."),

    // User Errors
    USER_NOT_FOUND("USER-001", "Usuário não encontrado."),

    // Statement Errors
    STATEMENT_NOT_FOUND("STATEMENT-001", "Extrato não encontrado."),
    INVALID_STATEMENT_DATE_RANGE("STATEMENT-002", "O período do extrato é inválido."),
    STATEMENT_DATE_RANGE_TOO_LARGE("STATEMENT-003", "O período do extrato não pode ser superior a 1 ano."),
    STATEMENT_GENERATION_FAILED("STATEMENT-004", "Falha ao gerar o extrato.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

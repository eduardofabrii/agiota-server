package com.agiota.bank.model.notification;

public enum NotificationType {

    USER_CREATED("Usuário Criado"),

    ACCOUNT_CREATED("Conta Criada"),
    ACCOUNT_UPDATED("Conta Atualizada"),
    ACCOUNT_DELETED("Conta Excluída"),
    
    PIX_KEY_CREATED("Chave PIX Criada"),
    PIX_KEY_DELETED("Chave PIX Excluída"),
    
    TRANSACTION_SENT("Transação Enviada"),
    TRANSACTION_RECEIVED("Transação Recebida"),
    TRANSACTION_FAILED("Falha na Transação"),
    
    CARD_CREATED("Cartão Criado"),
    CARD_UPDATED("Cartão Atualizado"),
    CARD_DELETED("Cartão Excluído"),
    
    BENEFICIARY_ADDED("Beneficiário Adicionado"),
    BENEFICIARY_UPDATED("Beneficiário Atualizado"),
    BENEFICIARY_DELETED("Beneficiário Removido"),
    
    SECURITY_ALERT("Alerta de Segurança"),
    SYSTEM_MAINTENANCE("Manutenção do Sistema");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
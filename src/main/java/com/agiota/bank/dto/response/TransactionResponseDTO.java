package com.agiota.bank.dto.response;

import com.agiota.bank.model.transaction.TransactionType;

import java.math.BigDecimal;

public record TransactionResponseDTO(
        Long id,
        BigDecimal value,
        String originAgency,
        String originAccountNumber,
        String destinationAgency,
        String destinationAccountNumber,
        TransactionType type,
        String status
) {
}

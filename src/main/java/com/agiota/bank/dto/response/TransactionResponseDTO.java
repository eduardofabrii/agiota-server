package com.agiota.bank.dto.response;

import com.agiota.bank.model.transaction.TransactionType;

import java.math.BigDecimal;

public record TransactionResponseDTO(
        Long id,
        BigDecimal value,
        String destinationPixKey,
        String destinationBank,
        String destinationAgency,
        String destinationAccount,
        TransactionType transactionType,
        String status
) {
}

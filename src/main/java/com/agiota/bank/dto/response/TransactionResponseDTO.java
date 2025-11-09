package com.agiota.bank.dto.response;

import com.agiota.bank.model.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDTO(
        Long id,
        BigDecimal amount,
        String originAccountAgency,
        String originAccountNumber,
        String destinationAccountAgency,
        String destinationAccountNumber,
        TransactionType type,
        String status,
        LocalDateTime date
) {
}

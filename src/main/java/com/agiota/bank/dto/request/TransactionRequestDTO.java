package com.agiota.bank.dto.request;

import com.agiota.bank.model.transaction.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionRequestDTO(
        @NotNull Long originAccountId,
        @NotNull @Positive BigDecimal amount,
        String destinationPixKey,
        String destinationAgency,
        String destinationAccountNumber,
        @NotNull TransactionType type
) {

}

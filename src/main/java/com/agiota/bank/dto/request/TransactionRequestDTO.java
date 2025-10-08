package com.agiota.bank.dto.request;

import com.agiota.bank.model.transaction.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionRequestDTO(
        @NotNull @Positive BigDecimal value,
        String destinationPixKey,
        String destinationBank,
        String destinationAgency,
        String destinationAccount,
        @NotNull TransactionType transactionType
) {

}
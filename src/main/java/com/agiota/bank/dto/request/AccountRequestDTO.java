package com.agiota.bank.dto.request;

import com.agiota.bank.model.account.AccountStatus;
import com.agiota.bank.model.account.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AccountRequestDTO(
        @NotNull(message = "ID do usuário é obrigatório")
        @Positive(message = "ID do usuário deve ser positivo")
        Long userId,

        String agency,

        String accountNumber,

        @NotNull(message = "Tipo de conta é obrigatório")
        AccountType accountType,

        @DecimalMin(value = "0.0", inclusive = true, message = "Saldo não pode ser negativo")
        BigDecimal balance,

        AccountStatus status
) {
}
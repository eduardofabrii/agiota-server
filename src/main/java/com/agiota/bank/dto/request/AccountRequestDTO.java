package com.agiota.bank.dto.request;

import com.agiota.bank.model.account.AccountStatus;
import com.agiota.bank.model.account.AccountType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AccountRequestDTO(
        @NotNull Long userId,
        String agency,
        String accountNumber,
        @NotNull AccountType accountType,
        BigDecimal balance,
        AccountStatus status
) {
}
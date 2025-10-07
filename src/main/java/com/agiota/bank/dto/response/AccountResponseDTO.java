package com.agiota.bank.dto.response;

import com.agiota.bank.model.account.AccountStatus;
import com.agiota.bank.model.account.AccountType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponseDTO(
        Long id,
        String agency,
        String accountNumber,
        BigDecimal balance,
        AccountType accountType,
        AccountStatus status,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") LocalDateTime createdAt
) {
}
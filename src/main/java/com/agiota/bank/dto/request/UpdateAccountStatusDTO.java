package com.agiota.bank.dto.request;

import com.agiota.bank.model.account.AccountStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateAccountStatusDTO(
        @NotNull(message = "Status é obrigatório")
        AccountStatus status
) {
}


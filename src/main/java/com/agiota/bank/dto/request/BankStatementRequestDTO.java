package com.agiota.bank.dto.request;

import com.agiota.bank.model.statement.StatementType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BankStatementRequestDTO(
        @NotNull(message = "O ID da conta é obrigatório")
        Long accountId,

        @NotNull(message = "A data de início é obrigatória")
        LocalDateTime startDate,

        @NotNull(message = "A data final é obrigatória")
        LocalDateTime endDate,

        @NotNull(message = "O tipo de extrato é obrigatório")
        StatementType type
) {
}


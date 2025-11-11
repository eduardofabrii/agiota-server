package com.agiota.bank.dto.response;

import com.agiota.bank.model.statement.StatementStatus;
import com.agiota.bank.model.statement.StatementType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BankStatementResponseDTO(
        Long id,
        Long accountId,
        String accountAgency,
        String accountNumber,
        LocalDateTime startDate,
        LocalDateTime endDate,
        StatementType type,
        StatementStatus status,
        LocalDateTime generatedAt,
        BigDecimal totalIncoming,
        BigDecimal totalOutgoing,
        BigDecimal currentBalance,
        List<TransactionResponseDTO> transactions
) {
}


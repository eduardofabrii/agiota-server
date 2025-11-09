package com.agiota.bank.dto.response;

import com.agiota.bank.model.loan.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LoanResponseDTO(
        Long id,
        Long accountId,
        BigDecimal amount,
        BigDecimal interestRate,
        Integer installments,
        Integer paidInstallments,
        BigDecimal installmentValue,
        LoanStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

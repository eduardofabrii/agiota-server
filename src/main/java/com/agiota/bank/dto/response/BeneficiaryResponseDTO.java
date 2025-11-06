package com.agiota.bank.dto.response;

import com.agiota.bank.model.account.AccountType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record BeneficiaryResponseDTO(
        Long id,
        Long ownerAccountId,
        String name,
        String cpfCnpj,
        String bankCode,
        String agency,
        String accountNumber,
        AccountType accountType,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") LocalDateTime createdAt,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") LocalDateTime updatedAt
) {
}
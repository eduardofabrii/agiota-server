package com.agiota.bank.dto.request;

import com.agiota.bank.model.account.AccountType;

public record BeneficiaryUpdateRequestDTO(
        String name,
        String cpfCnpj,
        String bankCode,
        String agency,
        String accountNumber,
        AccountType accountType
) {
}
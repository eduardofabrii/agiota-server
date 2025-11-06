package com.agiota.bank.dto.request;

import com.agiota.bank.model.account.AccountType;
import jakarta.validation.constraints.NotBlank;

public record BeneficiaryRequestDTO(
        @NotBlank String name,
        @NotBlank String cpfCnpj,
        @NotBlank String bankCode,
        @NotBlank String agency,
        @NotBlank String accountNumber,
        @NotBlank AccountType accountType
) {
}
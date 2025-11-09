package com.agiota.bank.dto.request;

import com.agiota.bank.model.loan.LoanStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AdminLoanApprovalRequestDTO(
        @NotNull
        LoanStatus status,

        @DecimalMin(value = "0.0")
        BigDecimal interestRate
) {
}

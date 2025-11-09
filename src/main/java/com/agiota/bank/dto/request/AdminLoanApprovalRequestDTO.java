package com.agiota.bank.dto.request;

import com.agiota.bank.model.loan.LoanStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminLoanApprovalRequestDTO {

    @NotNull
    private LoanStatus status; // Deve ser APPROVED ou REJECTED

    @DecimalMin(value = "0.0")
    private BigDecimal interestRate;
}

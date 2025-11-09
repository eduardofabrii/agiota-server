package com.agiota.bank.dto.request;

import com.agiota.bank.model.loan.LoanStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanRequestDTO {

    @NotNull
    private Long accountId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal interestRate;

    @NotNull
    @Future
    private LocalDate dueDate;

    private LoanStatus status;
}

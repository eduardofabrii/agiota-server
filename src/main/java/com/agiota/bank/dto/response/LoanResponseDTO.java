package com.agiota.bank.dto.response;

import com.agiota.bank.model.loan.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanResponseDTO {

    private Long id;
    private Long accountId;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private Integer installments;
    private Integer paidInstallments;
    private BigDecimal installmentValue;
    private LoanStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

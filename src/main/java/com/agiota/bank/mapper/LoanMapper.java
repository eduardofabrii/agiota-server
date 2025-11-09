package com.agiota.bank.mapper;

import com.agiota.bank.dto.request.CreateLoanRequestDTO;
import com.agiota.bank.dto.response.LoanResponseDTO;
import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.loan.Loan;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {

    public Loan toEntity(CreateLoanRequestDTO dto, Account account) {
        Loan loan = new Loan();
        loan.setAccount(account);
        loan.setAmount(dto.getAmount());
        loan.setInstallments(dto.getInstallments());
        return loan;
    }

    public LoanResponseDTO toResponse(Loan entity) {
        LoanResponseDTO response = new LoanResponseDTO();
        response.setId(entity.getId());
        response.setAccountId(entity.getAccount().getId());
        response.setAmount(entity.getAmount());
        response.setInterestRate(entity.getInterestRate());
        response.setInstallments(entity.getInstallments());
        response.setPaidInstallments(entity.getPaidInstallments());
        response.setInstallmentValue(entity.getInstallmentValue());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}

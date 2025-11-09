package com.agiota.bank.mapper;

import com.agiota.bank.dto.request.CreateLoanRequestDTO;
import com.agiota.bank.dto.response.LoanResponseDTO;
import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.loan.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LoanMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "interestRate", ignore = true)
    @Mapping(target = "paidInstallments", ignore = true)
    @Mapping(target = "installmentValue", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "account", source = "account")
    Loan toEntity(CreateLoanRequestDTO dto, Account account);

    @Mapping(source = "account.id", target = "accountId")
    LoanResponseDTO toResponse(Loan entity);
}

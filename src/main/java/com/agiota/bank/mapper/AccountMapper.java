package com.agiota.bank.mapper;

import com.agiota.bank.dto.request.AccountRequestDTO;
import com.agiota.bank.dto.response.AccountResponseDTO;
import com.agiota.bank.model.account.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "agency", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Account toAccount(AccountRequestDTO requestDTO);

    AccountResponseDTO toAccountResponse(Account account);

    List<AccountResponseDTO> toAccountResponseList(List<Account> accounts);
}
package com.agiota.bank.mapper;

import com.agiota.bank.dto.request.TransactionRequestDTO;
import com.agiota.bank.dto.response.TransactionResponseDTO;
import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.transaction.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "amount", source = "dto.amount")
    @Mapping(target = "originAccount", source = "originAccount")
    @Mapping(target = "destinationAccount", source = "destinationAccount")
    Transaction toTransaction(TransactionRequestDTO dto, Account originAccount, Account destinationAccount);

    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "originAccount.agency", target = "originAccountAgency")
    @Mapping(source = "originAccount.accountNumber", target = "originAccountNumber")
    @Mapping(source = "destinationAccount.agency", target = "destinationAccountAgency")
    @Mapping(source = "destinationAccount.accountNumber", target = "destinationAccountNumber")

    TransactionResponseDTO toTransactionResponse(Transaction transaction);

    List<TransactionResponseDTO> toTransactionListResponse(List<Transaction> transactions);
}

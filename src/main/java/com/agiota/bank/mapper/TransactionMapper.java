package com.agiota.bank.mapper;

import com.agiota.bank.dto.request.TransactionRequestDTO;
import com.agiota.bank.dto.response.TransactionResponseDTO;
import com.agiota.bank.model.transaction.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", constant = "Success")
    @Mapping(target = "originAccount", source = "originAccount")
    @Mapping(target = "destinationAccount", source = "destinationAccount")
    Transaction toTransactionPostRequest(TransactionRequestDTO dto, com.agiota.bank.model.account.Account originAccount, com.agiota.bank.model.account.Account destinationAccount);
    
    @Mapping(target = "originAgency", source = "originAccount.agency")
    @Mapping(target = "originAccountNumber", source = "originAccount.accountNumber")
    @Mapping(target = "destinationAgency", source = "destinationAccount.agency")
    @Mapping(target = "destinationAccountNumber", source = "destinationAccount.accountNumber")
    TransactionResponseDTO toTransactionPostResponse(Transaction transaction);
    
    List<TransactionResponseDTO> toTransactionListResponse(List<Transaction> transactions);
}

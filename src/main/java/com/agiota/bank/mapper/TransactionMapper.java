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
    Transaction toTransactionPostRequest(TransactionRequestDTO dto, Long originUserId, Long destinationUserId);
    TransactionResponseDTO toTransactionPostResponse(Transaction transaction);
    List<TransactionResponseDTO> toTransactionListResponse(List<Transaction> transactions);
}

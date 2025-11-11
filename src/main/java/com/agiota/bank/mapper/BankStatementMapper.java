package com.agiota.bank.mapper;

import com.agiota.bank.dto.request.BankStatementRequestDTO;
import com.agiota.bank.dto.response.BankStatementResponseDTO;
import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.statement.BankStatement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BankStatementMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "generatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "account", source = "account")
    @Mapping(target = "startDate", source = "dto.startDate")
    @Mapping(target = "endDate", source = "dto.endDate")
    @Mapping(target = "type", source = "dto.type")
    BankStatement toBankStatement(BankStatementRequestDTO dto, Account account);

    List<BankStatementResponseDTO> toBankStatementListResponse(List<BankStatement> statements);
}


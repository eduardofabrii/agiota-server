package com.agiota.bank.mapper;

import com.agiota.bank.dto.request.BeneficiaryRequestDTO;
import com.agiota.bank.dto.response.BeneficiaryResponseDTO;
import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.beneficiary.Beneficiary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BeneficiaryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerAccount", source = "ownerAccount")
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "cpfCnpj", source = "dto.cpfCnpj")
    @Mapping(target = "bankCode", source = "dto.bankCode")
    @Mapping(target = "agency", source = "dto.agency")
    @Mapping(target = "accountNumber", source = "dto.accountNumber")
    @Mapping(target = "accountType", source = "dto.accountType")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Beneficiary toBeneficiary(BeneficiaryRequestDTO dto, Account ownerAccount);

    @Mapping(target = "ownerAccountId", source = "ownerAccount.id")
    BeneficiaryResponseDTO toBeneficiaryResponse(Beneficiary beneficiary);

    List<BeneficiaryResponseDTO> toBeneficiaryListResponse(List<Beneficiary> beneficiaries);
}
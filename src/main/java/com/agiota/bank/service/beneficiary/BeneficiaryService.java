package com.agiota.bank.service.beneficiary;

import com.agiota.bank.dto.request.BeneficiaryRequestDTO;
import com.agiota.bank.dto.request.BeneficiaryUpdateRequestDTO;
import com.agiota.bank.dto.response.BeneficiaryResponseDTO;

import java.util.List;

public interface BeneficiaryService {
    List<BeneficiaryResponseDTO> listByOwnerAccount(Long ownerAccountId);
    BeneficiaryResponseDTO create(Long ownerAccountId, BeneficiaryRequestDTO requestDTO);
    BeneficiaryResponseDTO findById(Long id, Long ownerAccountId);
    BeneficiaryResponseDTO update(Long id, Long ownerAccountId, BeneficiaryUpdateRequestDTO requestDTO);
    void delete(Long id, Long ownerAccountId);
}
package com.agiota.bank.service.beneficiary;

import com.agiota.bank.dto.request.BeneficiaryRequestDTO;
import com.agiota.bank.dto.response.BeneficiaryResponseDTO;
import com.agiota.bank.exception.ResourceAlreadyExistsException;
import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.mapper.BeneficiaryMapper;
import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.beneficiary.Beneficiary;
import com.agiota.bank.repository.AccountRepository;
import com.agiota.bank.repository.BeneficiaryRepository;
import com.agiota.bank.service.notification.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final AccountRepository accountRepository;
    private final BeneficiaryMapper beneficiaryMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public List<BeneficiaryResponseDTO> listByOwnerAccount(Long ownerAccountId) {
        validateAccountExists(ownerAccountId);
        List<Beneficiary> beneficiaries = beneficiaryRepository.findByOwnerAccountId(ownerAccountId);
        return beneficiaryMapper.toBeneficiaryListResponse(beneficiaries);
    }

    @Override
    @Transactional
    public BeneficiaryResponseDTO create(Long ownerAccountId, BeneficiaryRequestDTO requestDTO) {
        Account ownerAccount = validateAccountExists(ownerAccountId);
        
        validateBeneficiaryData(requestDTO);
        
        String normalizedCpfCnpj = normalizeCpfCnpj(requestDTO.cpfCnpj());
        
        if (beneficiaryRepository.existsByCpfCnpj(normalizedCpfCnpj)) {
            throw new ResourceAlreadyExistsException("Beneficiary with CPF/CNPJ " + normalizedCpfCnpj + " already exists");
        }

        BeneficiaryRequestDTO normalizedDTO = new BeneficiaryRequestDTO(
            requestDTO.name(),
            normalizedCpfCnpj,
            requestDTO.bankCode(),
            requestDTO.agency(),
            requestDTO.accountNumber(),
            requestDTO.accountType()
        );

        Beneficiary beneficiary = beneficiaryMapper.toBeneficiary(normalizedDTO, ownerAccount);
        Beneficiary savedBeneficiary = beneficiaryRepository.save(beneficiary);
        
        notificationService.notifyBeneficiaryAdded(ownerAccount.getUser(), savedBeneficiary.getName());
        
        return beneficiaryMapper.toBeneficiaryResponse(savedBeneficiary);
    }

    @Override
    @Transactional(readOnly = true)
    public BeneficiaryResponseDTO findById(Long id, Long ownerAccountId) {
        validateAccountExists(ownerAccountId);
        Beneficiary beneficiary = findBeneficiaryByIdAndOwnerAccount(id, ownerAccountId);
        return beneficiaryMapper.toBeneficiaryResponse(beneficiary);
    }

    @Override
    @Transactional
    public BeneficiaryResponseDTO update(Long id, Long ownerAccountId, BeneficiaryRequestDTO requestDTO) {
        validateAccountExists(ownerAccountId);
        Beneficiary existingBeneficiary = findBeneficiaryByIdAndOwnerAccount(id, ownerAccountId);

        validateBeneficiaryData(requestDTO);

        String normalizedCpfCnpj = normalizeCpfCnpj(requestDTO.cpfCnpj());

        if (!existingBeneficiary.getCpfCnpj().equals(normalizedCpfCnpj) && 
            beneficiaryRepository.existsByCpfCnpj(normalizedCpfCnpj)) {
            throw new ResourceAlreadyExistsException("Beneficiary with CPF/CNPJ " + normalizedCpfCnpj + " already exists");
        }

        existingBeneficiary.setName(requestDTO.name());
        existingBeneficiary.setCpfCnpj(normalizedCpfCnpj);
        existingBeneficiary.setBankCode(requestDTO.bankCode());
        existingBeneficiary.setAgency(requestDTO.agency());
        existingBeneficiary.setAccountNumber(requestDTO.accountNumber());
        existingBeneficiary.setAccountType(requestDTO.accountType());
        existingBeneficiary.setUpdatedAt(LocalDateTime.now());

        Beneficiary updatedBeneficiary = beneficiaryRepository.save(existingBeneficiary);
        
        notificationService.notifyBeneficiaryUpdated(existingBeneficiary.getOwnerAccount().getUser(), updatedBeneficiary.getName());
        
        return beneficiaryMapper.toBeneficiaryResponse(updatedBeneficiary);
    }

    @Override
    @Transactional
    public void delete(Long id, Long ownerAccountId) {
        validateAccountExists(ownerAccountId);
        Beneficiary beneficiary = findBeneficiaryByIdAndOwnerAccount(id, ownerAccountId);
        
        notificationService.notifyBeneficiaryDeleted(beneficiary.getOwnerAccount().getUser(), beneficiary.getName());
        
        
        beneficiaryRepository.delete(beneficiary);
    }

    private Account validateAccountExists(Long ownerAccountId) {
        return accountRepository.findById(ownerAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + ownerAccountId));
    }

    private Beneficiary findBeneficiaryByIdAndOwnerAccount(Long id, Long ownerAccountId) {
        return beneficiaryRepository.findById(id)
                .filter(beneficiary -> beneficiary.getOwnerAccount().getId().equals(ownerAccountId))
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with id: " + id + " for account: " + ownerAccountId));
    }

    private String normalizeCpfCnpj(String cpfCnpj) {
        return cpfCnpj != null ? cpfCnpj.replaceAll("[^0-9]", "") : null;
    }

    private void validateBeneficiaryData(BeneficiaryRequestDTO requestDTO) {
        if (requestDTO.name() == null || requestDTO.name().trim().length() < 2 || requestDTO.name().trim().length() > 100) {
            throw new IllegalArgumentException("Nome deve ter entre 2 e 100 caracteres");
        }

        String normalizedCpfCnpj = normalizeCpfCnpj(requestDTO.cpfCnpj());
        if (normalizedCpfCnpj == null || (normalizedCpfCnpj.length() != 11 && normalizedCpfCnpj.length() != 14)) {
            throw new IllegalArgumentException("CPF deve ter 11 dígitos ou CNPJ deve ter 14 dígitos");
        }
        if (!normalizedCpfCnpj.matches("^[0-9]+$")) {
            throw new IllegalArgumentException("CPF/CNPJ deve conter apenas números");
        }

        if (requestDTO.bankCode() == null || requestDTO.bankCode().length() != 3 || !requestDTO.bankCode().matches("^[0-9]+$")) {
            throw new IllegalArgumentException("Código do banco deve ter exatamente 3 dígitos");
        }

        if (requestDTO.agency() == null || requestDTO.agency().length() < 4 || requestDTO.agency().length() > 5 || !requestDTO.agency().matches("^[0-9]+$")) {
            throw new IllegalArgumentException("Agência deve ter entre 4 e 5 dígitos");
        }

        if (requestDTO.accountNumber() == null || requestDTO.accountNumber().length() < 4 || requestDTO.accountNumber().length() > 20 || !requestDTO.accountNumber().matches("^[0-9]+$")) {
            throw new IllegalArgumentException("Número da conta deve ter entre 4 e 20 dígitos");
        }

        if (requestDTO.accountType() == null) {
            throw new IllegalArgumentException("Tipo da conta é obrigatório");
        }
    }
}
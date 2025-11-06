package com.agiota.bank.service.beneficiary;

import com.agiota.bank.dto.request.BeneficiaryRequestDTO;
import com.agiota.bank.dto.response.BeneficiaryResponseDTO;
import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.mapper.BeneficiaryMapper;
import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.account.AccountType;
import com.agiota.bank.model.beneficiary.Beneficiary;
import com.agiota.bank.repository.AccountRepository;
import com.agiota.bank.repository.BeneficiaryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeneficiaryServiceTest {

    @InjectMocks
    private BeneficiaryServiceImpl beneficiaryService;

    @Mock
    private BeneficiaryRepository beneficiaryRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BeneficiaryMapper beneficiaryMapper;

    private Beneficiary mockBeneficiary;
    private Account mockOwnerAccount;
    private BeneficiaryRequestDTO mockBeneficiaryRequest;
    private BeneficiaryResponseDTO mockBeneficiaryResponse;
    private Long ownerAccountId = 1L;

    @BeforeEach
    void setUp() {
        mockOwnerAccount = new Account();
        mockOwnerAccount.setId(ownerAccountId);

        mockBeneficiary = new Beneficiary();
        mockBeneficiary.setId(1L);
        mockBeneficiary.setOwnerAccount(mockOwnerAccount);
        mockBeneficiary.setName("João Silva");
        mockBeneficiary.setCpfCnpj("12345678901");
        mockBeneficiary.setBankCode("001");
        mockBeneficiary.setAgency("1234");
        mockBeneficiary.setAccountNumber("123456789");  // Número com 9 dígitos
        mockBeneficiary.setAccountType(AccountType.CORRENTE);
        mockBeneficiary.setCreatedAt(LocalDateTime.now());
        mockBeneficiary.setUpdatedAt(LocalDateTime.now());

        mockBeneficiaryRequest = new BeneficiaryRequestDTO(
                "João Silva",
                "12345678901",
                "001",
                "1234",
                "123456789",  // Número com 9 dígitos
                AccountType.CORRENTE
        );

        mockBeneficiaryResponse = new BeneficiaryResponseDTO(
                1L,
                ownerAccountId,
                "João Silva",
                "12345678901",
                "001",
                "1234",
                "123456789",  // Número com 9 dígitos
                AccountType.CORRENTE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void listByOwnerAccount_shouldReturnBeneficiaries() {
        // Arrange
        List<Beneficiary> beneficiaries = Arrays.asList(mockBeneficiary);
        
        when(accountRepository.findById(ownerAccountId)).thenReturn(Optional.of(mockOwnerAccount));
        when(beneficiaryRepository.findByOwnerAccountId(ownerAccountId)).thenReturn(beneficiaries);
        when(beneficiaryMapper.toBeneficiaryListResponse(beneficiaries)).thenReturn(Arrays.asList(mockBeneficiaryResponse));

        // Act
        List<BeneficiaryResponseDTO> result = beneficiaryService.listByOwnerAccount(ownerAccountId);

        // Assert
        assertThat(result).hasSize(1);
        verify(accountRepository).findById(ownerAccountId);
        verify(beneficiaryRepository).findByOwnerAccountId(ownerAccountId);
    }

    @Test
    void create_shouldCreateBeneficiary() {
        // Arrange
        when(accountRepository.findById(ownerAccountId)).thenReturn(Optional.of(mockOwnerAccount));
        when(beneficiaryRepository.existsByCpfCnpj(anyString())).thenReturn(false);
        when(beneficiaryMapper.toBeneficiary(any(BeneficiaryRequestDTO.class), eq(mockOwnerAccount))).thenReturn(mockBeneficiary);
        when(beneficiaryRepository.save(any(Beneficiary.class))).thenReturn(mockBeneficiary);
        when(beneficiaryMapper.toBeneficiaryResponse(mockBeneficiary)).thenReturn(mockBeneficiaryResponse);

        // Act
        BeneficiaryResponseDTO result = beneficiaryService.create(ownerAccountId, mockBeneficiaryRequest);

        // Assert
        assertThat(result).isEqualTo(mockBeneficiaryResponse);
        verify(accountRepository).findById(ownerAccountId);
        verify(beneficiaryRepository).save(any(Beneficiary.class));
        verify(beneficiaryMapper).toBeneficiaryResponse(mockBeneficiary);
    }

    @Test
    void findById_shouldReturnBeneficiary_whenBeneficiaryExists() {
        // Arrange
        when(accountRepository.findById(ownerAccountId)).thenReturn(Optional.of(mockOwnerAccount));
        when(beneficiaryRepository.findById(1L)).thenReturn(Optional.of(mockBeneficiary));
        when(beneficiaryMapper.toBeneficiaryResponse(mockBeneficiary)).thenReturn(mockBeneficiaryResponse);

        // Act
        BeneficiaryResponseDTO result = beneficiaryService.findById(1L, ownerAccountId);

        // Assert
        assertThat(result).isEqualTo(mockBeneficiaryResponse);
        verify(accountRepository).findById(ownerAccountId);
        verify(beneficiaryRepository).findById(1L);
        verify(beneficiaryMapper).toBeneficiaryResponse(mockBeneficiary);
    }

    @Test
    void findById_shouldThrowException_whenBeneficiaryNotExists() {
        // Arrange
        when(accountRepository.findById(ownerAccountId)).thenReturn(Optional.of(mockOwnerAccount));
        when(beneficiaryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> beneficiaryService.findById(1L, ownerAccountId))
                .isInstanceOf(ResourceNotFoundException.class);
        
        verify(accountRepository).findById(ownerAccountId);
        verify(beneficiaryRepository).findById(1L);
        verify(beneficiaryMapper, never()).toBeneficiaryResponse(any());
    }

    @Test
    void update_shouldUpdateBeneficiary_whenBeneficiaryExists() {
        // Arrange
        when(accountRepository.findById(ownerAccountId)).thenReturn(Optional.of(mockOwnerAccount));
        when(beneficiaryRepository.findById(1L)).thenReturn(Optional.of(mockBeneficiary));
        when(beneficiaryRepository.save(any(Beneficiary.class))).thenReturn(mockBeneficiary);
        when(beneficiaryMapper.toBeneficiaryResponse(mockBeneficiary)).thenReturn(mockBeneficiaryResponse);

        // Act
        BeneficiaryResponseDTO result = beneficiaryService.update(1L, ownerAccountId, mockBeneficiaryRequest);

        // Assert
        assertThat(result).isEqualTo(mockBeneficiaryResponse);
        verify(accountRepository).findById(ownerAccountId);
        verify(beneficiaryRepository).findById(1L);
        verify(beneficiaryRepository).save(mockBeneficiary);
        verify(beneficiaryMapper).toBeneficiaryResponse(mockBeneficiary);
    }

    @Test
    void update_shouldThrowException_whenBeneficiaryNotExists() {
        // Arrange
        when(accountRepository.findById(ownerAccountId)).thenReturn(Optional.of(mockOwnerAccount));
        when(beneficiaryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> beneficiaryService.update(1L, ownerAccountId, mockBeneficiaryRequest))
                .isInstanceOf(ResourceNotFoundException.class);
        
        verify(accountRepository).findById(ownerAccountId);
        verify(beneficiaryRepository).findById(1L);
        verify(beneficiaryRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteBeneficiary_whenBeneficiaryExists() {
        // Arrange
        when(accountRepository.findById(ownerAccountId)).thenReturn(Optional.of(mockOwnerAccount));
        when(beneficiaryRepository.findById(1L)).thenReturn(Optional.of(mockBeneficiary));

        // Act
        beneficiaryService.delete(1L, ownerAccountId);

        // Assert
        verify(accountRepository).findById(ownerAccountId);
        verify(beneficiaryRepository).findById(1L);
        verify(beneficiaryRepository).delete(mockBeneficiary);
    }

    @Test
    void delete_shouldThrowException_whenBeneficiaryNotExists() {
        // Arrange
        when(accountRepository.findById(ownerAccountId)).thenReturn(Optional.of(mockOwnerAccount));
        when(beneficiaryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> beneficiaryService.delete(1L, ownerAccountId))
                .isInstanceOf(ResourceNotFoundException.class);
        
        verify(accountRepository).findById(ownerAccountId);
        verify(beneficiaryRepository).findById(1L);
        verify(beneficiaryRepository, never()).delete(any());
    }
}
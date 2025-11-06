package com.agiota.bank.controller;

import com.agiota.bank.dto.request.BeneficiaryRequestDTO;
import com.agiota.bank.dto.response.BeneficiaryResponseDTO;
import com.agiota.bank.model.account.AccountType;
import com.agiota.bank.service.beneficiary.BeneficiaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeneficiaryControllerTest {

    @Mock
    private BeneficiaryService beneficiaryService;

    @InjectMocks
    private BeneficiaryController beneficiaryController;

    private BeneficiaryRequestDTO requestDTO;
    private BeneficiaryResponseDTO responseDTO;
    private Long ownerAccountId;

    @BeforeEach
    void setUp() {
        ownerAccountId = 100L;
        requestDTO = new BeneficiaryRequestDTO(
                "João Silva", "12345678901", "001", "1234", "123456789", AccountType.CORRENTE
        );
        responseDTO = new BeneficiaryResponseDTO(
                1L, ownerAccountId, "João Silva", "12345678901", 
                "001", "1234", "123456789", AccountType.CORRENTE,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void findById_shouldReturnBeneficiary() {
        // Given
        Long beneficiaryId = 1L;
        when(beneficiaryService.findById(beneficiaryId, ownerAccountId)).thenReturn(responseDTO);

        // When
        ResponseEntity<BeneficiaryResponseDTO> response = beneficiaryController.findById(beneficiaryId, ownerAccountId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(beneficiaryService).findById(beneficiaryId, ownerAccountId);
    }

    @Test
    void create_shouldCreateBeneficiary() throws Exception {
        // Given
        when(beneficiaryService.create(eq(ownerAccountId), any(BeneficiaryRequestDTO.class))).thenReturn(responseDTO);

        // When
        ResponseEntity<BeneficiaryResponseDTO> response = beneficiaryController.create(ownerAccountId, requestDTO);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(beneficiaryService).create(ownerAccountId, requestDTO);
    }

    @Test
    void update_shouldUpdateBeneficiary() {
        // Given
        Long beneficiaryId = 1L;
        when(beneficiaryService.update(eq(beneficiaryId), eq(ownerAccountId), any(BeneficiaryRequestDTO.class))).thenReturn(responseDTO);

        // When
        ResponseEntity<BeneficiaryResponseDTO> response = beneficiaryController.update(beneficiaryId, ownerAccountId, requestDTO);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(beneficiaryService).update(beneficiaryId, ownerAccountId, requestDTO);
    }

    @Test
    void delete_shouldDeleteBeneficiary() {
        // Given
        Long beneficiaryId = 1L;
        doNothing().when(beneficiaryService).delete(beneficiaryId, ownerAccountId);

        // When
        ResponseEntity<Void> response = beneficiaryController.delete(beneficiaryId, ownerAccountId);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(beneficiaryService).delete(beneficiaryId, ownerAccountId);
    }

    @Test
    void listByOwnerAccount_shouldReturnBeneficiaries() {
        // Given
        List<BeneficiaryResponseDTO> beneficiaries = Arrays.asList(responseDTO);
        when(beneficiaryService.listByOwnerAccount(ownerAccountId)).thenReturn(beneficiaries);

        // When
        ResponseEntity<List<BeneficiaryResponseDTO>> response = beneficiaryController.listByOwnerAccount(ownerAccountId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(beneficiaries, response.getBody());
        verify(beneficiaryService).listByOwnerAccount(ownerAccountId);
    }
}
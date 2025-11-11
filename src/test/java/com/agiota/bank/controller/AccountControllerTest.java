package com.agiota.bank.controller;

import com.agiota.bank.dto.request.AccountRequestDTO;
import com.agiota.bank.dto.request.UpdateAccountStatusDTO;
import com.agiota.bank.dto.response.AccountResponseDTO;
import com.agiota.bank.model.account.AccountStatus;
import com.agiota.bank.model.account.AccountType;
import com.agiota.bank.service.account.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private AccountRequestDTO requestDTO;
    private AccountResponseDTO responseDTO;
    private UpdateAccountStatusDTO updateStatusDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new AccountRequestDTO(
                1L,
                "0001",
                "12345678",
                AccountType.CORRENTE,
                BigDecimal.valueOf(1000.00),
                AccountStatus.ATIVO
        );

        responseDTO = new AccountResponseDTO(
                1L,
                "0001",
                "12345678",
                BigDecimal.valueOf(1000.00),
                AccountType.CORRENTE,
                AccountStatus.ATIVO,
                LocalDateTime.now()
        );

        updateStatusDTO = new UpdateAccountStatusDTO(AccountStatus.BLOQUEADO);
    }

    @Test
    void create_shouldCreateAccount() throws Exception {
        // Given
        when(accountService.create(any(AccountRequestDTO.class))).thenReturn(responseDTO);

        // When
        ResponseEntity<AccountResponseDTO> response = accountController.create(requestDTO);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(responseDTO, response.getBody());
        verify(accountService).create(requestDTO);
    }

    @Test
    void findById_shouldReturnAccount() {
        // Given
        Long accountId = 1L;
        when(accountService.findById(accountId)).thenReturn(responseDTO);

        // When
        ResponseEntity<AccountResponseDTO> response = accountController.findById(accountId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(accountService).findById(accountId);
    }

    @Test
    void findByUserId_shouldReturnAccounts() {
        // Given
        Long userId = 1L;
        List<AccountResponseDTO> accounts = Arrays.asList(responseDTO);
        when(accountService.findByUserId(userId)).thenReturn(accounts);

        // When
        ResponseEntity<List<AccountResponseDTO>> response = accountController.findByUserId(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(accounts, response.getBody());
        verify(accountService).findByUserId(userId);
    }

    @Test
    void findAll_shouldReturnAllAccounts() {
        // Given
        List<AccountResponseDTO> accounts = Arrays.asList(responseDTO);
        when(accountService.findAll()).thenReturn(accounts);

        // When
        ResponseEntity<List<AccountResponseDTO>> response = accountController.findAll();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(accounts, response.getBody());
        verify(accountService).findAll();
    }

    @Test
    void update_shouldUpdateAccount() {
        // Given
        Long accountId = 1L;
        when(accountService.update(eq(accountId), any(AccountRequestDTO.class))).thenReturn(responseDTO);

        // When
        ResponseEntity<AccountResponseDTO> response = accountController.update(accountId, requestDTO);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(accountService).update(accountId, requestDTO);
    }

    @Test
    void updateStatus_shouldUpdateAccountStatus() {
        // Given
        Long accountId = 1L;
        AccountResponseDTO blockedResponse = new AccountResponseDTO(
                1L,
                "0001",
                "12345678",
                BigDecimal.valueOf(1000.00),
                AccountType.CORRENTE,
                AccountStatus.BLOQUEADO,
                LocalDateTime.now()
        );
        when(accountService.updateStatus(eq(accountId), any(UpdateAccountStatusDTO.class))).thenReturn(blockedResponse);

        // When
        ResponseEntity<AccountResponseDTO> response = accountController.updateStatus(accountId, updateStatusDTO);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(blockedResponse, response.getBody());
        assertNotNull(response.getBody());
        assertEquals(AccountStatus.BLOQUEADO, response.getBody().status());
        verify(accountService).updateStatus(accountId, updateStatusDTO);
    }

    @Test
    void delete_shouldDeleteAccount() {
        // Given
        Long accountId = 1L;
        doNothing().when(accountService).delete(accountId);

        // When
        ResponseEntity<Void> response = accountController.delete(accountId);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(accountService).delete(accountId);
    }

    @Test
    void getBalance_shouldReturnAccountWithBalance() {
        // Given
        Long accountId = 1L;
        when(accountService.findById(accountId)).thenReturn(responseDTO);

        // When
        ResponseEntity<AccountResponseDTO> response = accountController.getBalance(accountId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().balance());
        verify(accountService).findById(accountId);
    }
}


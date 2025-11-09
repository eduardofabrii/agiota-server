package com.agiota.bank.controller;

import com.agiota.bank.dto.request.TransactionRequestDTO;
import com.agiota.bank.dto.response.TransactionResponseDTO;
import com.agiota.bank.model.transaction.TransactionType;
import com.agiota.bank.model.user.User;
import com.agiota.bank.service.transaction.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private User mockUser;
    private TransactionRequestDTO requestDTO;
    private TransactionResponseDTO responseDTO;
    private Long accountId = 1L;
    private Long transactionId = 1L;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);

        requestDTO = new TransactionRequestDTO(
                accountId,
                new BigDecimal("100.00"),
                "pix-key",
                null,
                null,
                TransactionType.PIX
        );

        responseDTO = new TransactionResponseDTO(
                transactionId,
                new BigDecimal("100.00"),
                "0001",
                "12345-6",
                "0002",
                "78910-1",
                TransactionType.PIX,
                "Success",
                LocalDateTime.now()
        );

        // Mock do contexto da requisição HTTP para o build da URI
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void getById_shouldReturnTransaction() {
        // Arrange
        when(transactionService.listTransactionById(transactionId)).thenReturn(responseDTO);

        // Act
        ResponseEntity<TransactionResponseDTO> response = transactionController.getById(transactionId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDTO);
        verify(transactionService).listTransactionById(transactionId);
    }

    @Test
    void listUserTransactionsSent_shouldReturnTransactions() {
        // Arrange
        List<TransactionResponseDTO> transactions = Collections.singletonList(responseDTO);
        when(transactionService.listAccountTransactionsSent(accountId)).thenReturn(transactions);

        // Act
        ResponseEntity<List<TransactionResponseDTO>> response = transactionController.listUserTransactionsSent(accountId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(transactions);
        verify(transactionService).listAccountTransactionsSent(accountId);
    }

    @Test
    void listUserTransactionsReceived_shouldReturnTransactions() {
        // Arrange
        List<TransactionResponseDTO> transactions = Collections.singletonList(responseDTO);
        when(transactionService.listAccountTransactionsReceived(accountId)).thenReturn(transactions);

        // Act
        ResponseEntity<List<TransactionResponseDTO>> response = transactionController.listUserTransactionsReceived(accountId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(transactions);
        verify(transactionService).listAccountTransactionsReceived(accountId);
    }

    @Test
    void create_shouldCreateTransaction() {
        // Arrange
        when(transactionService.create(any(TransactionRequestDTO.class), any(User.class))).thenReturn(responseDTO);

        // Act
        ResponseEntity<TransactionResponseDTO> response = transactionController.create(requestDTO, mockUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(responseDTO);
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/" + transactionId);
        verify(transactionService).create(eq(requestDTO), eq(mockUser));
    }

    @Test
    void update_shouldUpdateTransaction() {
        // Arrange
        when(transactionService.update(eq(transactionId), any(TransactionRequestDTO.class))).thenReturn(responseDTO);

        // Act
        ResponseEntity<TransactionResponseDTO> response = transactionController.update(requestDTO, transactionId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDTO);
        verify(transactionService).update(eq(transactionId), eq(requestDTO));
    }

    @Test
    void delete_shouldDeleteTransaction() {
        // Arrange
        doNothing().when(transactionService).delete(transactionId);

        // Act
        ResponseEntity<Void> response = transactionController.delete(transactionId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(transactionService).delete(transactionId);
    }
}

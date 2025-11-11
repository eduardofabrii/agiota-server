package com.agiota.bank.service.transaction;

import com.agiota.bank.dto.request.TransactionRequestDTO;
import com.agiota.bank.dto.response.TransactionResponseDTO;
import com.agiota.bank.exception.InvalidBankDataException;
import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.mapper.TransactionMapper;
import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.pixkey.PixKey;
import com.agiota.bank.model.transaction.Transaction;
import com.agiota.bank.model.transaction.TransactionType;
import com.agiota.bank.model.user.User;
import com.agiota.bank.repository.AccountRepository;
import com.agiota.bank.repository.PixKeyRepository;
import com.agiota.bank.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PixKeyRepository pixKeyRepository;

    @Mock
    private TransactionMapper transactionMapper;

    private User user;
    private Account originAccount;
    private Account destinationAccount;
    private TransactionRequestDTO transactionRequest;
    private Transaction transaction;
    private TransactionResponseDTO transactionResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        originAccount = new Account();
        originAccount.setId(1L);
        originAccount.setUser(user);
        originAccount.setBalance(new BigDecimal("1000.00"));
        originAccount.setAgency("0001");
        originAccount.setAccountNumber("123456");

        destinationAccount = new Account();
        destinationAccount.setId(2L);
        destinationAccount.setBalance(new BigDecimal("500.00"));
        destinationAccount.setAgency("0002");
        destinationAccount.setAccountNumber("789101");

        transactionRequest = new TransactionRequestDTO(
                originAccount.getId(),
                new BigDecimal("100.00"),
                "valid-pix-key",
                null,
                null,
                TransactionType.PIX
        );

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setOriginAccount(originAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setDate(LocalDateTime.now());
        transaction.setStatus("Success");
        transaction.setType(TransactionType.PIX);

        transactionResponse = new TransactionResponseDTO(
                1L,
                new BigDecimal("100.00"),
                originAccount.getAgency(),
                originAccount.getAccountNumber(),
                destinationAccount.getAgency(),
                destinationAccount.getAccountNumber(),
                TransactionType.PIX,
                "Success",
                transaction.getDate()
        );
    }

    @Test
    void listAllTransactions_shouldReturnAllTransactions() {
        // Arrange
        List<Transaction> transactions = Collections.singletonList(transaction);
        List<TransactionResponseDTO> transactionResponses = Collections.singletonList(transactionResponse);
        when(transactionRepository.findAll()).thenReturn(transactions);
        when(transactionMapper.toTransactionListResponse(transactions)).thenReturn(transactionResponses);

        // Act
        List<TransactionResponseDTO> result = transactionService.listAllTransactions();

        // Assert
        assertThat(result).isEqualTo(transactionResponses);
        verify(transactionRepository).findAll();
        verify(transactionMapper).toTransactionListResponse(transactions);
    }

    @Test
    void create_shouldPerformTransaction_whenPixAndDataIsValid() {
        // Arrange
        PixKey pixKey = new PixKey();
        pixKey.setAccount(destinationAccount);

        when(accountRepository.findById(originAccount.getId())).thenReturn(Optional.of(originAccount));
        when(pixKeyRepository.findByKeyValue("valid-pix-key")).thenReturn(Optional.of(pixKey));
        when(transactionMapper.toTransaction(any(), any(), any())).thenReturn(transaction);
        when(transactionRepository.save(any())).thenReturn(transaction);
        when(transactionMapper.toTransactionResponse(any())).thenReturn(transactionResponse);

        // Act
        TransactionResponseDTO result = transactionService.create(transactionRequest, user);

        // Assert
        assertThat(result).isEqualTo(transactionResponse);
        assertThat(originAccount.getBalance()).isEqualByComparingTo(new BigDecimal("900.00"));
        assertThat(destinationAccount.getBalance()).isEqualByComparingTo(new BigDecimal("600.00"));
        verify(accountRepository, times(2)).save(any(Account.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void create_shouldThrowException_whenBalanceIsInsufficient() {
        // Arrange
        originAccount.setBalance(new BigDecimal("50.00"));
        when(accountRepository.findById(originAccount.getId())).thenReturn(Optional.of(originAccount));

        // Act & Assert
        assertThatThrownBy(() -> transactionService.create(transactionRequest, user))
                .isInstanceOf(InvalidBankDataException.class)
                .hasMessage("Saldo insuficiente para realizar a transação.");

        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowException_whenOriginAccountNotBelongsToUser() {
        // Arrange
        User anotherUser = new User();
        anotherUser.setId(99L);
        when(accountRepository.findById(originAccount.getId())).thenReturn(Optional.of(originAccount));

        // Act & Assert
        assertThatThrownBy(() -> transactionService.create(transactionRequest, anotherUser))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Você não tem permissão para realizar transações a partir desta conta.");
    }

    @Test
    void create_shouldThrowException_whenPixKeyIsNotFound() {
        // Arrange
        when(accountRepository.findById(originAccount.getId())).thenReturn(Optional.of(originAccount));
        when(pixKeyRepository.findByKeyValue(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> transactionService.create(transactionRequest, user))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Chave Pix não encontrada.");
    }
    
    @Test
    void update_shouldUpdateTransaction() {
        // Arrange
        TransactionRequestDTO updateRequest = new TransactionRequestDTO(null, new BigDecimal("250.00"), null, null, null, TransactionType.TED);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(transactionMapper.toTransactionResponse(any(Transaction.class))).thenReturn(transactionResponse);

        // Act
        TransactionResponseDTO result = transactionService.update(1L, updateRequest);

        // Assert
        verify(transactionRepository).findById(1L);
        verify(transactionRepository).save(transaction);
        assertThat(transaction.getAmount()).isEqualByComparingTo(new BigDecimal("250.00"));
        assertThat(transaction.getType()).isEqualTo(TransactionType.TED);
        assertThat(result).isEqualTo(transactionResponse);
    }

    @Test
    void delete_shouldDeleteTransaction() {
        // Arrange
        when(transactionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(transactionRepository).deleteById(1L);

        // Act
        transactionService.delete(1L);

        // Assert
        verify(transactionRepository).existsById(1L);
        verify(transactionRepository).deleteById(1L);
    }
}

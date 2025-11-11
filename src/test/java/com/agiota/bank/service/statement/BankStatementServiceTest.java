package com.agiota.bank.service.statement;

import com.agiota.bank.dto.request.BankStatementRequestDTO;
import com.agiota.bank.dto.response.BankStatementResponseDTO;
import com.agiota.bank.exception.InvalidBankDataException;
import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.mapper.BankStatementMapper;
import com.agiota.bank.mapper.TransactionMapper;
import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.account.AccountStatus;
import com.agiota.bank.model.account.AccountType;
import com.agiota.bank.model.statement.BankStatement;
import com.agiota.bank.model.statement.StatementStatus;
import com.agiota.bank.model.statement.StatementType;
import com.agiota.bank.model.transaction.Transaction;
import com.agiota.bank.model.transaction.TransactionType;
import com.agiota.bank.model.user.User;
import com.agiota.bank.repository.AccountRepository;
import com.agiota.bank.repository.BankStatementRepository;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankStatementServiceTest {

    @InjectMocks
    private BankStatementServiceImpl bankStatementService;

    @Mock
    private BankStatementRepository bankStatementRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BankStatementMapper bankStatementMapper;

    @Mock
    private TransactionMapper transactionMapper;

    private User user;
    private Account account;
    private BankStatementRequestDTO statementRequest;
    private BankStatement bankStatement;
    private Transaction transaction1;
    private Transaction transaction2;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        account = new Account();
        account.setId(1L);
        account.setUser(user);
        account.setAgency("0001");
        account.setAccountNumber("12345-6");
        account.setAccountType(AccountType.CORRENTE);
        account.setBalance(new BigDecimal("2000.00"));
        account.setStatus(AccountStatus.ATIVO);

        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        statementRequest = new BankStatementRequestDTO(
                account.getId(),
                startDate,
                endDate,
                StatementType.MENSAL
        );

        bankStatement = new BankStatement();
        bankStatement.setId(1L);
        bankStatement.setAccount(account);
        bankStatement.setStartDate(startDate);
        bankStatement.setEndDate(endDate);
        bankStatement.setType(StatementType.MENSAL);
        bankStatement.setStatus(StatementStatus.GERADO);
        bankStatement.setGeneratedAt(LocalDateTime.now());

        Account destinationAccount = new Account();
        destinationAccount.setId(2L);
        destinationAccount.setAgency("0002");
        destinationAccount.setAccountNumber("78910-1");
        destinationAccount.setBalance(new BigDecimal("1000.00"));

        transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setAmount(new BigDecimal("100.00"));
        transaction1.setOriginAccount(account);
        transaction1.setDestinationAccount(destinationAccount);
        transaction1.setType(TransactionType.PIX);
        transaction1.setStatus("Success");
        transaction1.setDate(LocalDateTime.now().minusDays(5));

        transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setAmount(new BigDecimal("200.00"));
        transaction2.setOriginAccount(destinationAccount);
        transaction2.setDestinationAccount(account);
        transaction2.setType(TransactionType.TED);
        transaction2.setStatus("Success");
        transaction2.setDate(LocalDateTime.now().minusDays(3));
    }

    @Test
    void generate_ShouldGenerateStatementSuccessfully() {
        // Arrange
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(bankStatementMapper.toBankStatement(statementRequest, account)).thenReturn(bankStatement);
        when(bankStatementRepository.save(any(BankStatement.class))).thenReturn(bankStatement);
        when(transactionRepository.findByAccountIdAndDateBetween(
                account.getId(),
                statementRequest.startDate(),
                statementRequest.endDate()
        )).thenReturn(List.of(transaction1, transaction2));
        when(transactionMapper.toTransactionListResponse(anyList())).thenReturn(List.of());

        // Act
        BankStatementResponseDTO response = bankStatementService.generate(statementRequest, user);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.accountId()).isEqualTo(account.getId());
        verify(bankStatementRepository, times(1)).save(any(BankStatement.class));
        verify(transactionRepository, times(1)).findByAccountIdAndDateBetween(anyLong(), any(), any());
    }

    @Test
    void generate_ShouldThrowException_WhenStartDateAfterEndDate() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().minusDays(30);
        BankStatementRequestDTO invalidRequest = new BankStatementRequestDTO(
                account.getId(),
                startDate,
                endDate,
                StatementType.MENSAL
        );

        // Act & Assert
        assertThatThrownBy(() -> bankStatementService.generate(invalidRequest, user))
                .isInstanceOf(InvalidBankDataException.class)
                .hasMessage("A data de início não pode ser posterior à data final.");
    }

    @Test
    void generate_ShouldThrowException_WhenEndDateInFuture() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        BankStatementRequestDTO invalidRequest = new BankStatementRequestDTO(
                account.getId(),
                startDate,
                endDate,
                StatementType.MENSAL
        );

        // Act & Assert
        assertThatThrownBy(() -> bankStatementService.generate(invalidRequest, user))
                .isInstanceOf(InvalidBankDataException.class)
                .hasMessage("A data final não pode ser posterior à data atual.");
    }

    @Test
    void generate_ShouldThrowException_WhenDateRangeExceedsOneYear() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusYears(2);
        LocalDateTime endDate = LocalDateTime.now();
        BankStatementRequestDTO invalidRequest = new BankStatementRequestDTO(
                account.getId(),
                startDate,
                endDate,
                StatementType.PERSONALIZADO
        );

        // Act & Assert
        assertThatThrownBy(() -> bankStatementService.generate(invalidRequest, user))
                .isInstanceOf(InvalidBankDataException.class)
                .hasMessage("O período do extrato não pode ser superior a 1 ano.");
    }

    @Test
    void generate_ShouldThrowException_WhenAccountNotFound() {
        // Arrange
        when(accountRepository.findById(account.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> bankStatementService.generate(statementRequest, user))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Conta não encontrada");
    }

    @Test
    void generate_ShouldThrowException_WhenUserNotAccountOwner() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(2L);
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        // Act & Assert
        assertThatThrownBy(() -> bankStatementService.generate(statementRequest, otherUser))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Você não tem permissão para gerar extratos desta conta.");
    }

    @Test
    void getById_ShouldReturnStatement_WhenStatementExists() {
        // Arrange
        when(bankStatementRepository.findById(bankStatement.getId())).thenReturn(Optional.of(bankStatement));
        when(transactionRepository.findByAccountIdAndDateBetween(
                account.getId(),
                bankStatement.getStartDate(),
                bankStatement.getEndDate()
        )).thenReturn(List.of(transaction1, transaction2));
        when(transactionMapper.toTransactionListResponse(anyList())).thenReturn(List.of());

        // Act
        BankStatementResponseDTO response = bankStatementService.getById(bankStatement.getId(), user);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(bankStatement.getId());
        verify(bankStatementRepository, times(1)).findById(bankStatement.getId());
    }

    @Test
    void getById_ShouldThrowException_WhenStatementNotFound() {
        // Arrange
        when(bankStatementRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> bankStatementService.getById(999L, user))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Extrato não encontrado");
    }

    @Test
    void getById_ShouldThrowException_WhenUserNotAuthorized() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(2L);
        when(bankStatementRepository.findById(bankStatement.getId())).thenReturn(Optional.of(bankStatement));

        // Act & Assert
        assertThatThrownBy(() -> bankStatementService.getById(bankStatement.getId(), otherUser))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Você não tem permissão para visualizar este extrato.");
    }

    @Test
    void getByAccountId_ShouldReturnStatementsList() {
        // Arrange
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(bankStatementRepository.findByAccountId(account.getId()))
                .thenReturn(List.of(bankStatement));
        when(transactionRepository.findByAccountIdAndDateBetween(anyLong(), any(), any()))
                .thenReturn(List.of(transaction1, transaction2));
        when(transactionMapper.toTransactionListResponse(anyList())).thenReturn(List.of());

        // Act
        List<BankStatementResponseDTO> response = bankStatementService.getByAccountId(account.getId(), user);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);
        verify(bankStatementRepository, times(1)).findByAccountId(account.getId());
    }

    @Test
    void getByAccountIdAndStatus_ShouldReturnFilteredStatements() {
        // Arrange
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(bankStatementRepository.findByAccountIdAndStatus(account.getId(), StatementStatus.GERADO))
                .thenReturn(List.of(bankStatement));
        when(transactionRepository.findByAccountIdAndDateBetween(anyLong(), any(), any()))
                .thenReturn(List.of(transaction1, transaction2));
        when(transactionMapper.toTransactionListResponse(anyList())).thenReturn(List.of());

        // Act
        List<BankStatementResponseDTO> response = bankStatementService.getByAccountIdAndStatus(
                account.getId(), StatementStatus.GERADO, user);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);
        verify(bankStatementRepository, times(1)).findByAccountIdAndStatus(account.getId(), StatementStatus.GERADO);
    }

    @Test
    void updateStatus_ShouldUpdateStatementStatus() {
        // Arrange
        when(bankStatementRepository.findById(bankStatement.getId())).thenReturn(Optional.of(bankStatement));
        when(bankStatementRepository.save(any(BankStatement.class))).thenReturn(bankStatement);
        when(transactionRepository.findByAccountIdAndDateBetween(anyLong(), any(), any()))
                .thenReturn(List.of(transaction1, transaction2));
        when(transactionMapper.toTransactionListResponse(anyList())).thenReturn(List.of());

        // Act
        BankStatementResponseDTO response = bankStatementService.updateStatus(
                bankStatement.getId(), StatementStatus.VISUALIZADO, user);

        // Assert
        assertThat(response).isNotNull();
        verify(bankStatementRepository, times(1)).save(any(BankStatement.class));
    }

    @Test
    void updateStatus_ShouldThrowException_WhenUserNotAuthorized() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(2L);
        when(bankStatementRepository.findById(bankStatement.getId())).thenReturn(Optional.of(bankStatement));

        // Act & Assert
        assertThatThrownBy(() -> bankStatementService.updateStatus(
                bankStatement.getId(), StatementStatus.VISUALIZADO, otherUser))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Você não tem permissão para atualizar este extrato.");
    }

    @Test
    void delete_ShouldDeleteStatement() {
        // Arrange
        when(bankStatementRepository.findById(bankStatement.getId())).thenReturn(Optional.of(bankStatement));
        doNothing().when(bankStatementRepository).deleteById(bankStatement.getId());

        // Act
        bankStatementService.delete(bankStatement.getId(), user);

        // Assert
        verify(bankStatementRepository, times(1)).deleteById(bankStatement.getId());
    }

    @Test
    void delete_ShouldThrowException_WhenStatementNotFound() {
        // Arrange
        when(bankStatementRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> bankStatementService.delete(999L, user))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Extrato não encontrado");
    }

    @Test
    void delete_ShouldThrowException_WhenUserNotAuthorized() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(2L);
        when(bankStatementRepository.findById(bankStatement.getId())).thenReturn(Optional.of(bankStatement));

        // Act & Assert
        assertThatThrownBy(() -> bankStatementService.delete(bankStatement.getId(), otherUser))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Você não tem permissão para excluir este extrato.");
    }
}


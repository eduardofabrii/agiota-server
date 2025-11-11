package com.agiota.bank.service.account;

import com.agiota.bank.dto.request.AccountRequestDTO;
import com.agiota.bank.dto.request.UpdateAccountStatusDTO;
import com.agiota.bank.dto.response.AccountResponseDTO;
import com.agiota.bank.exception.AccountException;
import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.mapper.AccountMapper;
import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.account.AccountStatus;
import com.agiota.bank.model.account.AccountType;
import com.agiota.bank.model.user.User;
import com.agiota.bank.repository.AccountRepository;
import com.agiota.bank.repository.UserRepository;
import com.agiota.bank.service.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private NotificationService notificationService;

    private User mockUser;
    private Account mockAccount;
    private AccountRequestDTO mockAccountRequest;
    private AccountResponseDTO mockAccountResponse;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Test User");
        mockUser.setEmail("test@example.com");

        mockAccount = new Account();
        mockAccount.setId(1L);
        mockAccount.setUser(mockUser);
        mockAccount.setAgency("0001");
        mockAccount.setAccountNumber("12345678");
        mockAccount.setAccountType(AccountType.CORRENTE);
        mockAccount.setBalance(BigDecimal.valueOf(1000.00));
        mockAccount.setStatus(AccountStatus.ATIVO);
        mockAccount.setCreatedAt(LocalDateTime.now());

        mockAccountRequest = new AccountRequestDTO(
                1L,
                "0001",
                "12345678",
                AccountType.CORRENTE,
                BigDecimal.valueOf(1000.00),
                AccountStatus.ATIVO
        );

        mockAccountResponse = new AccountResponseDTO(
                1L,
                "0001",
                "12345678",
                BigDecimal.valueOf(1000.00),
                AccountType.CORRENTE,
                AccountStatus.ATIVO,
                LocalDateTime.now()
        );
    }

    @Test
    void create_shouldCreateAccount_whenValidRequest() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(accountMapper.toAccount(mockAccountRequest)).thenReturn(mockAccount);
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);
        when(accountMapper.toAccountResponse(mockAccount)).thenReturn(mockAccountResponse);
        doNothing().when(notificationService).notifyAccountCreated(any(User.class), anyString(), anyString());

        // Act
        AccountResponseDTO result = accountService.create(mockAccountRequest);

        // Assert
        assertThat(result).isEqualTo(mockAccountResponse);
        verify(userRepository).findById(1L);
        verify(accountMapper).toAccount(mockAccountRequest);
        verify(accountRepository).save(any(Account.class));
        verify(accountMapper).toAccountResponse(mockAccount);
        verify(notificationService).notifyAccountCreated(any(User.class), anyString(), anyString());
    }

    @Test
    void create_shouldThrowException_whenUserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accountService.create(mockAccountRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findById(1L);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowException_whenAccountTypeIsNull() {
        // Arrange
        AccountRequestDTO invalidRequest = new AccountRequestDTO(
                1L, "0001", "12345678", null, BigDecimal.ZERO, AccountStatus.ATIVO
        );
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // Act & Assert
        assertThatThrownBy(() -> accountService.create(invalidRequest))
                .isInstanceOf(AccountException.class);

        verify(userRepository).findById(1L);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void findById_shouldReturnAccount_whenAccountExists() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
        when(accountMapper.toAccountResponse(mockAccount)).thenReturn(mockAccountResponse);

        // Act
        AccountResponseDTO result = accountService.findById(1L);

        // Assert
        assertThat(result).isEqualTo(mockAccountResponse);
        verify(accountRepository).findById(1L);
        verify(accountMapper).toAccountResponse(mockAccount);
    }

    @Test
    void findById_shouldThrowException_whenAccountNotExists() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accountService.findById(1L))
                .isInstanceOf(AccountException.class);

        verify(accountRepository).findById(1L);
        verify(accountMapper, never()).toAccountResponse(any());
    }

    @Test
    void findByUserId_shouldReturnAccounts_whenUserExists() {
        // Arrange
        List<Account> accounts = Arrays.asList(mockAccount);
        List<AccountResponseDTO> expectedResponse = Arrays.asList(mockAccountResponse);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(accountRepository.findByUserId(1L)).thenReturn(accounts);
        when(accountMapper.toAccountResponseList(accounts)).thenReturn(expectedResponse);

        // Act
        List<AccountResponseDTO> result = accountService.findByUserId(1L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(expectedResponse);
        verify(userRepository).existsById(1L);
        verify(accountRepository).findByUserId(1L);
        verify(accountMapper).toAccountResponseList(accounts);
    }

    @Test
    void findByUserId_shouldThrowException_whenUserNotExists() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> accountService.findByUserId(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).existsById(1L);
        verify(accountRepository, never()).findByUserId(any());
    }

    @Test
    void findAll_shouldReturnAllAccounts() {
        // Arrange
        List<Account> accounts = Arrays.asList(mockAccount);
        List<AccountResponseDTO> expectedResponse = Arrays.asList(mockAccountResponse);

        when(accountRepository.findAll()).thenReturn(accounts);
        when(accountMapper.toAccountResponseList(accounts)).thenReturn(expectedResponse);

        // Act
        List<AccountResponseDTO> result = accountService.findAll();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(expectedResponse);
        verify(accountRepository).findAll();
        verify(accountMapper).toAccountResponseList(accounts);
    }

    @Test
    void update_shouldUpdateAccount_whenAccountExists() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);
        when(accountMapper.toAccountResponse(mockAccount)).thenReturn(mockAccountResponse);
        doNothing().when(notificationService).notifyAccountUpdated(any(User.class), anyString());

        // Act
        AccountResponseDTO result = accountService.update(1L, mockAccountRequest);

        // Assert
        assertThat(result).isEqualTo(mockAccountResponse);
        verify(accountRepository).findById(1L);
        verify(accountRepository).save(mockAccount);
        verify(accountMapper).toAccountResponse(mockAccount);
        verify(notificationService).notifyAccountUpdated(any(User.class), anyString());
    }

    @Test
    void update_shouldThrowException_whenAccountNotExists() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accountService.update(1L, mockAccountRequest))
                .isInstanceOf(AccountException.class);

        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowException_whenAccountIsClosed() {
        // Arrange
        mockAccount.setStatus(AccountStatus.ENCERRADO);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));

        // Act & Assert
        assertThatThrownBy(() -> accountService.update(1L, mockAccountRequest))
                .isInstanceOf(AccountException.class);

        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void updateStatus_shouldUpdateAccountStatus_whenAccountExists() {
        // Arrange
        UpdateAccountStatusDTO statusDTO = new UpdateAccountStatusDTO(AccountStatus.BLOQUEADO);
        AccountResponseDTO blockedResponse = new AccountResponseDTO(
                1L, "0001", "12345678", BigDecimal.valueOf(1000.00),
                AccountType.CORRENTE, AccountStatus.BLOQUEADO, LocalDateTime.now()
        );

        when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);
        when(accountMapper.toAccountResponse(mockAccount)).thenReturn(blockedResponse);

        // Act
        AccountResponseDTO result = accountService.updateStatus(1L, statusDTO);

        // Assert
        assertThat(result).isEqualTo(blockedResponse);
        verify(accountRepository).findById(1L);
        verify(accountRepository).save(mockAccount);
        verify(accountMapper).toAccountResponse(mockAccount);
    }

    @Test
    void updateStatus_shouldThrowException_whenAccountIsClosed() {
        // Arrange
        mockAccount.setStatus(AccountStatus.ENCERRADO);
        UpdateAccountStatusDTO statusDTO = new UpdateAccountStatusDTO(AccountStatus.ATIVO);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));

        // Act & Assert
        assertThatThrownBy(() -> accountService.updateStatus(1L, statusDTO))
                .isInstanceOf(AccountException.class)
                .hasMessageContaining("Não é possível reativar conta encerrada");

        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void updateStatus_shouldThrowException_whenAccountNotExists() {
        // Arrange
        UpdateAccountStatusDTO statusDTO = new UpdateAccountStatusDTO(AccountStatus.BLOQUEADO);
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accountService.updateStatus(1L, statusDTO))
                .isInstanceOf(AccountException.class);

        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteAccount_whenAccountExistsWithZeroBalance() {
        // Arrange
        mockAccount.setBalance(BigDecimal.ZERO);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
        doNothing().when(accountRepository).delete(mockAccount);
        doNothing().when(notificationService).notifyAccountDeleted(any(User.class), anyString());

        // Act
        accountService.delete(1L);

        // Assert
        verify(accountRepository).findById(1L);
        verify(accountRepository).delete(mockAccount);
        verify(notificationService).notifyAccountDeleted(any(User.class), anyString());
    }

    @Test
    void delete_shouldThrowException_whenAccountNotExists() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accountService.delete(1L))
                .isInstanceOf(AccountException.class);

        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).delete(any());
    }

    @Test
    void delete_shouldThrowException_whenAccountHasBalance() {
        // Arrange
        mockAccount.setBalance(BigDecimal.valueOf(100.00));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));

        // Act & Assert
        assertThatThrownBy(() -> accountService.delete(1L))
                .isInstanceOf(AccountException.class);

        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).delete(any());
    }
}


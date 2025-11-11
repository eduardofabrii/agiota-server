package com.agiota.bank.service.account;

import com.agiota.bank.dto.request.AccountRequestDTO;
import com.agiota.bank.dto.request.UpdateAccountStatusDTO;
import com.agiota.bank.dto.response.AccountResponseDTO;
import com.agiota.bank.exception.AccountException;
import com.agiota.bank.exception.ErrorCode;
import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.mapper.AccountMapper;
import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.account.AccountStatus;
import com.agiota.bank.model.user.User;
import com.agiota.bank.repository.AccountRepository;
import com.agiota.bank.repository.UserRepository;
import com.agiota.bank.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public AccountResponseDTO create(AccountRequestDTO requestDTO) {
        log.info("Creating account for user ID: {}", requestDTO.userId());

        User user = userRepository.findById(requestDTO.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + requestDTO.userId()));

        // Validate account type
        if (requestDTO.accountType() == null) {
            throw new AccountException(ErrorCode.INVALID_ACCOUNT_TYPE);
        }

        Account account = accountMapper.toAccount(requestDTO);
        account.setUser(user);
        account.setAgency("0001");
        account.setAccountNumber(generateUniqueAccountNumber());

        // Set default balance if not provided
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }

        // Set default status if not provided
        if (account.getStatus() == null) {
            account.setStatus(AccountStatus.ATIVO);
        }

        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully: {}", savedAccount.getAccountNumber());

        notificationService.notifyAccountCreated(user, savedAccount.getAccountNumber(), savedAccount.getAgency());

        return accountMapper.toAccountResponse(savedAccount);
    }

    @Override
    public AccountResponseDTO findById(Long id) {
        log.info("Finding account by ID: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
        return accountMapper.toAccountResponse(account);
    }

    @Override
    public List<AccountResponseDTO> findByUserId(Long userId) {
        log.info("Finding accounts for user ID: {}", userId);

        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        List<Account> accounts = accountRepository.findByUserId(userId);
        return accountMapper.toAccountResponseList(accounts);
    }

    @Override
    public List<AccountResponseDTO> findAll() {
        log.info("Finding all accounts");
        List<Account> accounts = accountRepository.findAll();
        return accountMapper.toAccountResponseList(accounts);
    }


    @Override
    @Transactional
    public AccountResponseDTO update(Long id, AccountRequestDTO requestDTO) {
        log.info("Updating account ID: {}", id);

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        // Validate account is not closed
        if (account.getStatus() == AccountStatus.ENCERRADO) {
            throw new AccountException(ErrorCode.ACCOUNT_CLOSED);
        }

        updateAccountFields(account, requestDTO);

        Account updatedAccount = accountRepository.save(account);
        log.info("Account updated successfully: {}", updatedAccount.getAccountNumber());

        notificationService.notifyAccountUpdated(account.getUser(), updatedAccount.getAccountNumber());
        
        return accountMapper.toAccountResponse(updatedAccount);
    }

    @Override
    @Transactional
    public AccountResponseDTO updateStatus(Long id, UpdateAccountStatusDTO statusDTO) {
        log.info("Updating account status for ID: {} to {}", id, statusDTO.status());

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        AccountStatus newStatus = statusDTO.status();

        // Validate status transition
        if (account.getStatus() == AccountStatus.ENCERRADO && newStatus != AccountStatus.ENCERRADO) {
            throw new AccountException(ErrorCode.ACCOUNT_CLOSED, "- Não é possível reativar conta encerrada");
        }

        account.setStatus(newStatus);
        Account updatedAccount = accountRepository.save(account);

        log.info("Account status updated successfully: {} to {}", account.getAccountNumber(), newStatus);

        return accountMapper.toAccountResponse(updatedAccount);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting account ID: {}", id);

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        // Validate account can be deleted (no balance)
        if (account.getBalance() != null && account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new AccountException(ErrorCode.CANNOT_DELETE_ACCOUNT_WITH_BALANCE);
        }

        notificationService.notifyAccountDeleted(account.getUser(), account.getAccountNumber());
        
        accountRepository.delete(account);
        log.info("Account deleted successfully: {}", account.getAccountNumber());
    }

    private void updateAccountFields(Account account, AccountRequestDTO requestDTO) {
        Optional.ofNullable(requestDTO.userId())
                .ifPresent(userId -> updateUser(account, userId));

        Optional.ofNullable(requestDTO.agency())
                .filter(agency -> !agency.isBlank())
                .ifPresent(account::setAgency);

        Optional.ofNullable(requestDTO.accountNumber())
                .filter(accountNumber -> !accountNumber.isBlank())
                .ifPresent(newAccountNumber -> {
                    // Validate account number doesn't exist
                    if (accountRepository.findByAccountNumber(newAccountNumber).isPresent()
                            && !newAccountNumber.equals(account.getAccountNumber())) {
                        throw new AccountException(ErrorCode.ACCOUNT_NUMBER_ALREADY_EXISTS);
                    }
                    account.setAccountNumber(newAccountNumber);
                });

        Optional.ofNullable(requestDTO.accountType())
                .ifPresent(account::setAccountType);

        Optional.ofNullable(requestDTO.balance())
                .ifPresent(account::setBalance);

        Optional.ofNullable(requestDTO.status())
                .ifPresent(account::setStatus);
    }

    private void updateUser(Account account, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        account.setUser(user);
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        int attempts = 0;
        int maxAttempts = 10;

        do {
            accountNumber = generateAccountNumber();
            attempts++;

            if (attempts >= maxAttempts) {
                throw new AccountException(ErrorCode.ACCOUNT_NUMBER_ALREADY_EXISTS,
                        "- Não foi possível gerar número único após " + maxAttempts + " tentativas");
            }
        } while (accountRepository.findByAccountNumber(accountNumber).isPresent());

        return accountNumber;
    }

    private String generateAccountNumber() {
        Random random = new Random();
        int number = 10000000 + random.nextInt(90000000);
        return String.valueOf(number);
    }

    @Override
    @Transactional
    public AccountResponseDTO createDefaultAccountForUser(User user) {
        log.info("Creating default account for user ID: {}", user.getId());

        Account account = new Account();
        account.setUser(user);
        account.setAgency("0001");
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setAccountType(com.agiota.bank.model.account.AccountType.CORRENTE);
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.ATIVO);

        Account savedAccount = accountRepository.save(account);
        log.info("Default account created successfully: {}", savedAccount.getAccountNumber());

        notificationService.notifyAccountCreated(user, savedAccount.getAccountNumber(), savedAccount.getAgency());

        return accountMapper.toAccountResponse(savedAccount);
    }
}
package com.agiota.bank.service.account;

import com.agiota.bank.dto.request.AccountRequestDTO;
import com.agiota.bank.dto.response.AccountResponseDTO;
import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.mapper.AccountMapper;
import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.user.User;
import com.agiota.bank.repository.AccountRepository;
import com.agiota.bank.repository.UserRepository;
import com.agiota.bank.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;
    private final NotificationService notificationService;

    @Override
    public AccountResponseDTO create(AccountRequestDTO requestDTO) {
        User user = userRepository.findById(requestDTO.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + requestDTO.userId()));

        Account account = accountMapper.toAccount(requestDTO);

        account.setUser(user);
        account.setAgency("0001");
        account.setAccountNumber(generateAccountNumber());

        Account savedAccount = accountRepository.save(account);

        notificationService.notifyAccountCreated(user, savedAccount.getAccountNumber(), savedAccount.getAgency());

        return accountMapper.toAccountResponse(savedAccount);
    }

    @Override
    public AccountResponseDTO findById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        return accountMapper.toAccountResponse(account);
    }

    @Override
    public List<AccountResponseDTO> findByUserId(Long userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        return accountMapper.toAccountResponseList(accounts);
    }

    @Override
    public AccountResponseDTO update(Long id, AccountRequestDTO requestDTO) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        updateAccountFields(account, requestDTO);

        Account updatedAccount = accountRepository.save(account);
        

        notificationService.notifyAccountUpdated(account.getUser(), updatedAccount.getAccountNumber());
        
        return accountMapper.toAccountResponse(updatedAccount);
    }

    @Override
    public void delete(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        notificationService.notifyAccountDeleted(account.getUser(), account.getAccountNumber());
        
        accountRepository.delete(account);
    }

    private void updateAccountFields(Account account, AccountRequestDTO requestDTO) {
        Optional.ofNullable(requestDTO.userId())
                .ifPresent(userId -> updateUser(account, userId));

        Optional.ofNullable(requestDTO.agency())
                .filter(agency -> !agency.isBlank())
                .ifPresent(account::setAgency);

        Optional.ofNullable(requestDTO.accountNumber())
                .filter(accountNumber -> !accountNumber.isBlank())
                .ifPresent(account::setAccountNumber);

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

    private String generateAccountNumber() {
        Random random = new Random();
        int number = 10000000 + random.nextInt(90000000);
        return String.valueOf(number);
    }
}
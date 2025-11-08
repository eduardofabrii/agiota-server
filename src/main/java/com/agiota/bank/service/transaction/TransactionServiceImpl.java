package com.agiota.bank.service.transaction;

import com.agiota.bank.dto.request.TransactionRequestDTO;
import com.agiota.bank.dto.response.TransactionResponseDTO;
import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.mapper.TransactionMapper;
import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.pixkey.PixKey;
import com.agiota.bank.model.transaction.Transaction;
import com.agiota.bank.model.transaction.TransactionType;
import com.agiota.bank.repository.AccountRepository;
import com.agiota.bank.repository.PixKeyRepository;
import com.agiota.bank.repository.TransactionRepository;
import com.agiota.bank.service.notification.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final PixKeyRepository pixKeyRepository;
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;
    @Override
    public List<TransactionResponseDTO> listAccountTransactionsSent(Long acountId) {
        List<Transaction> transactions = transactionRepository.findByOriginAccountId(acountId);
        return transactionMapper.toTransactionListResponse(transactions);
    }

    @Override
    public List<TransactionResponseDTO> listAccountTransactionsReceived(Long accountId) {
        List<Transaction> transactions = transactionRepository.findByDestinationAccountId(accountId);
        return transactionMapper.toTransactionListResponse(transactions);
    }

    @Override
    public TransactionResponseDTO create(TransactionRequestDTO postRequest, Long originAccountId) {
        TransactionType type = postRequest.type();
        Long destinationAccountId;
        Account originAccount = accountRepository.findById(originAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Origin account not found"));
        Account destinationAccount;
        
        if (type == TransactionType.PIX) {
            String pixKey = postRequest.destinationPixKey();
            PixKey pixKeyEntity = pixKeyRepository.findByKeyValue(pixKey)
                    .orElseThrow(() -> new ResourceNotFoundException("PixKey not found"));
            destinationAccountId = pixKeyEntity.getAccount().getId();
            destinationAccount = pixKeyEntity.getAccount();
        } else {
            String agency = postRequest.destinationAgency();
            String accountNumber = postRequest.destinationAccountNumber();
            destinationAccount = accountRepository.findByAgencyAndAccountNumber(agency, accountNumber)
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
            destinationAccountId = destinationAccount.getId();
        }
        
        Transaction transaction = transactionMapper.toTransactionPostRequest(postRequest, originAccountId, destinationAccountId);
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        String transactionTypeStr = type.toString();
        double amount = postRequest.value().doubleValue();
        
        String destinationInfo = type == TransactionType.PIX ? 
                postRequest.destinationPixKey() : 
                destinationAccount.getAgency() + " / " + destinationAccount.getAccountNumber();
        notificationService.notifyTransactionSent(
                originAccount.getUser(), 
                amount, 
                destinationInfo, 
                transactionTypeStr
        );
        
        String originInfo = originAccount.getAgency() + " / " + originAccount.getAccountNumber();
        notificationService.notifyTransactionReceived(
                destinationAccount.getUser(), 
                amount, 
                originInfo, 
                transactionTypeStr
        );
        
        return transactionMapper.toTransactionPostResponse(savedTransaction);
    }

    @Override
    public TransactionResponseDTO listTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        return transactionMapper.toTransactionPostResponse(transaction);
    }

    @Override
    public TransactionResponseDTO update(Long id, TransactionRequestDTO dto) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        transactionRepository.save(transaction);
        return transactionMapper.toTransactionPostResponse(transaction);
    }

    @Override
    public void delete(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction not found");
        }
        transactionRepository.deleteById(id);
    }
}

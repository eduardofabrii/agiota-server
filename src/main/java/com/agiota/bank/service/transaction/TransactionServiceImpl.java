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
    public TransactionResponseDTO create(TransactionRequestDTO postRequest, Long originUserId) {
        TransactionType type = postRequest.type();
        Long destinationUserId;
        if (type == TransactionType.PIX) {
            String pixKey = postRequest.destinationPixKey();
            PixKey pixKeyEntity = pixKeyRepository.findByKeyValue(pixKey).orElseThrow(() -> new ResourceNotFoundException("PixKey not found"));
            destinationUserId = pixKeyEntity.getAccount().getId();
        }
        else {
            String agency = postRequest.destinationAgency();
            String accountNumber = postRequest.destinationAccountNumber();
            Account destinationAccount = accountRepository.findByAgencyAndAccountNumber(agency, accountNumber).orElseThrow(() -> new ResourceNotFoundException("Account not found"));
            destinationUserId = destinationAccount.getId();
        }
        Transaction transaction = transactionMapper.toTransactionPostRequest(postRequest, originUserId, destinationUserId);
        Transaction savedTransaction = transactionRepository.save(transaction);
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

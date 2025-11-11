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
import com.agiota.bank.service.notification.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final PixKeyRepository pixKeyRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<TransactionResponseDTO> listAccountTransactionsSent(Long accountId) {
        List<Transaction> transactions = transactionRepository.findByOriginAccountId(accountId);
        return transactionMapper.toTransactionListResponse(transactions);
    }

    @Override
    public List<TransactionResponseDTO> listAccountTransactionsReceived(Long accountId) {
        List<Transaction> transactions = transactionRepository.findByDestinationAccountId(accountId);
        return transactionMapper.toTransactionListResponse(transactions);
    }

    @Override
    public List<TransactionResponseDTO> listAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        return transactionMapper.toTransactionListResponse(transactions);
    }

    @Override
    @Transactional
    public TransactionResponseDTO create(TransactionRequestDTO request, User user) {
        Account originAccount = accountRepository.findById(request.originAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Conta de origem não encontrada com o ID: " + request.originAccountId()));

        if (!originAccount.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Você não tem permissão para realizar transações a partir desta conta.");
        }

        if (originAccount.getBalance().compareTo(request.amount()) < 0) {
            throw new InvalidBankDataException("Saldo insuficiente para realizar a transação.");
        }

        Account destinationAccount;
        if (request.type() == TransactionType.PIX) {
            PixKey pixKeyEntity = pixKeyRepository.findByKeyValue(request.destinationPixKey())
                    .orElseThrow(() -> new ResourceNotFoundException("Chave Pix não encontrada."));
            destinationAccount = pixKeyEntity.getAccount();
        } else {
            destinationAccount = accountRepository.findByAgencyAndAccountNumber(request.destinationAgency(), request.destinationAccountNumber())
                    .orElseThrow(() -> new ResourceNotFoundException("Conta de destino não encontrada."));
        }

        if (originAccount.getId().equals(destinationAccount.getId())) {
            throw new InvalidBankDataException("A conta de origem e destino não podem ser a mesma.");
        }

        originAccount.setBalance(originAccount.getBalance().subtract(request.amount()));
        destinationAccount.setBalance(destinationAccount.getBalance().add(request.amount()));

        accountRepository.save(originAccount);
        accountRepository.save(destinationAccount);

        Transaction transaction = transactionMapper.toTransaction(request, originAccount, destinationAccount);
        Transaction savedTransaction = transactionRepository.save(transaction);

        return transactionMapper.toTransactionResponse(savedTransaction);
    }

    @Override
    public TransactionResponseDTO listTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada com o ID: " + id));
        return transactionMapper.toTransactionResponse(transaction);
    }

    @Override
    @Transactional
    public TransactionResponseDTO update(Long id, TransactionRequestDTO dto) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada com o ID: " + id));
        if (dto.amount() != null) {
            transaction.setAmount(dto.amount());
        }
        if (dto.type() != null) {
            transaction.setType(dto.type());
        }
        
        Transaction updatedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toTransactionResponse(updatedTransaction);
    }

    @Override
    public void delete(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transação não encontrada com o ID: " + id);
        }
        transactionRepository.deleteById(id);
    }
}

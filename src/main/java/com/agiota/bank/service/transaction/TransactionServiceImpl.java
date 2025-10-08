package com.agiota.bank.service.transaction;

import com.agiota.bank.dto.request.TransactionRequestDTO;
import com.agiota.bank.dto.response.TransactionResponseDTO;
import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.mapper.TransactionMapper;
import com.agiota.bank.model.pixkey.PixKey;
import com.agiota.bank.model.transaction.Transaction;
import com.agiota.bank.repository.PixKeyRepository;
import com.agiota.bank.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final PixKeyRepository pixKeyRepository;
    @Override
    public List<TransactionResponseDTO> listUserTransactionsSent(Long ownerId) {
        List<Transaction> transactions = transactionRepository.findByOriginUserId(ownerId);
        return transactionMapper.toTransactionListResponse(transactions);
    }

    @Override
    public List<TransactionResponseDTO> listUserTransactionsReceived(Long ownerId) {
        List<Transaction> transactions = transactionRepository.findByDestinationUserId(ownerId);
        return transactionMapper.toTransactionListResponse(transactions);
    }

    @Override
    public TransactionResponseDTO create(TransactionRequestDTO postRequest, Long originUserId) {
        String pixKey = postRequest.destinationPixKey();
        PixKey pixKeyEntity = pixKeyRepository.findByKeyValue(pixKey).orElseThrow(() -> new ResourceNotFoundException("PixKey not found"));
        Long destinationUserId = pixKeyEntity.getAccount().getId();
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

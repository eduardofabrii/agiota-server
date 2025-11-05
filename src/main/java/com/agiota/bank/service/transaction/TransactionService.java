package com.agiota.bank.service.transaction;

import com.agiota.bank.dto.request.TransactionRequestDTO;
import com.agiota.bank.dto.response.TransactionResponseDTO;

import java.util.List;
public interface TransactionService {
    List<TransactionResponseDTO> listAccountTransactionsSent(Long accountId);
    List<TransactionResponseDTO> listAccountTransactionsReceived(Long accountId);
    TransactionResponseDTO create(TransactionRequestDTO postRequest, Long ownerId);
    TransactionResponseDTO listTransactionById(Long id);
    TransactionResponseDTO update(Long id, TransactionRequestDTO dto);
    void delete(Long id);
}

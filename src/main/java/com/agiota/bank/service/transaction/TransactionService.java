package com.agiota.bank.service.transaction;

import com.agiota.bank.dto.request.TransactionRequestDTO;
import com.agiota.bank.dto.response.TransactionResponseDTO;
import com.agiota.bank.model.user.User;

import java.util.List;

public interface TransactionService {
    List<TransactionResponseDTO> listAccountTransactionsSent(Long accountId);
    List<TransactionResponseDTO> listAccountTransactionsReceived(Long accountId);
    TransactionResponseDTO create(TransactionRequestDTO request, User user);
    TransactionResponseDTO listTransactionById(Long id);
    TransactionResponseDTO update(Long id, TransactionRequestDTO dto);
    void delete(Long id);
}

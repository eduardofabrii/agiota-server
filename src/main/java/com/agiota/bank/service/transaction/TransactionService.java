package com.agiota.bank.service.transaction;

import com.agiota.bank.dto.request.TransactionRequestDTO;
import com.agiota.bank.dto.response.TransactionResponseDTO;

import java.util.List;
import java.util.Optional;

public interface TransactionService {
    List<TransactionResponseDTO> listUserTransactionsSent(Long ownerId);
    List<TransactionResponseDTO> listUserTransactionsReceived(Long ownerId);
    TransactionResponseDTO create(TransactionRequestDTO postRequest, Long ownerId);
    TransactionResponseDTO listTransactionById(Long id);
    TransactionResponseDTO update(Long id, TransactionRequestDTO dto);
    void delete(Long id);
}

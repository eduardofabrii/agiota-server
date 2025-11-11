package com.agiota.bank.service.statement;

import com.agiota.bank.dto.request.BankStatementRequestDTO;
import com.agiota.bank.dto.response.BankStatementResponseDTO;
import com.agiota.bank.model.statement.StatementStatus;
import com.agiota.bank.model.user.User;

import java.util.List;

public interface BankStatementService {
    BankStatementResponseDTO generate(BankStatementRequestDTO request, User user);
    BankStatementResponseDTO getById(Long id, User user);
    List<BankStatementResponseDTO> getByAccountId(Long accountId, User user);
    List<BankStatementResponseDTO> getByAccountIdAndStatus(Long accountId, StatementStatus status, User user);
    BankStatementResponseDTO updateStatus(Long id, StatementStatus status, User user);
    void delete(Long id, User user);
}


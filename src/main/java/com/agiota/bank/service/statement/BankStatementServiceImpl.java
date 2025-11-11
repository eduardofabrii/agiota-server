package com.agiota.bank.service.statement;

import com.agiota.bank.dto.request.BankStatementRequestDTO;
import com.agiota.bank.dto.response.BankStatementResponseDTO;
import com.agiota.bank.dto.response.TransactionResponseDTO;
import com.agiota.bank.exception.InvalidBankDataException;
import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.exception.StatementException;
import com.agiota.bank.mapper.BankStatementMapper;
import com.agiota.bank.mapper.TransactionMapper;
import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.statement.BankStatement;
import com.agiota.bank.model.statement.StatementStatus;
import com.agiota.bank.model.transaction.Transaction;
import com.agiota.bank.model.user.User;
import com.agiota.bank.repository.AccountRepository;
import com.agiota.bank.repository.BankStatementRepository;
import com.agiota.bank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankStatementServiceImpl implements BankStatementService {
    private final BankStatementRepository bankStatementRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BankStatementMapper bankStatementMapper;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional
    public BankStatementResponseDTO generate(BankStatementRequestDTO request, User user) {
        // Validate dates
        if (request.startDate().isAfter(request.endDate())) {
            throw new InvalidBankDataException("A data de início não pode ser posterior à data final.");
        }

        if (request.endDate().isAfter(LocalDateTime.now())) {
            throw new InvalidBankDataException("A data final não pode ser posterior à data atual.");
        }

        // Validate date range (max 1 year)
        if (request.startDate().plusYears(1).isBefore(request.endDate())) {
            throw new InvalidBankDataException("O período do extrato não pode ser superior a 1 ano.");
        }

        // Find account and validate ownership
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada com o ID: " + request.accountId()));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Você não tem permissão para gerar extratos desta conta.");
        }

        // Create bank statement
        BankStatement statement = bankStatementMapper.toBankStatement(request, account);
        BankStatement savedStatement = bankStatementRepository.save(statement);

        // Get transactions in date range
        List<Transaction> transactions = transactionRepository.findByAccountIdAndDateBetween(
                account.getId(),
                request.startDate(),
                request.endDate()
        );

        return buildStatementResponse(savedStatement, transactions);
    }

    @Override
    @Transactional(readOnly = true)
    public BankStatementResponseDTO getById(Long id, User user) {
        BankStatement statement = bankStatementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Extrato não encontrado com o ID: " + id));

        if (!statement.getAccount().getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Você não tem permissão para visualizar este extrato.");
        }

        List<Transaction> transactions = transactionRepository.findByAccountIdAndDateBetween(
                statement.getAccount().getId(),
                statement.getStartDate(),
                statement.getEndDate()
        );

        return buildStatementResponse(statement, transactions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankStatementResponseDTO> getByAccountId(Long accountId, User user) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada com o ID: " + accountId));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Você não tem permissão para visualizar extratos desta conta.");
        }

        List<BankStatement> statements = bankStatementRepository.findByAccountId(accountId);

        return statements.stream()
                .map(statement -> {
                    List<Transaction> transactions = transactionRepository.findByAccountIdAndDateBetween(
                            accountId,
                            statement.getStartDate(),
                            statement.getEndDate()
                    );
                    return buildStatementResponse(statement, transactions);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankStatementResponseDTO> getByAccountIdAndStatus(Long accountId, StatementStatus status, User user) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada com o ID: " + accountId));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Você não tem permissão para visualizar extratos desta conta.");
        }

        List<BankStatement> statements = bankStatementRepository.findByAccountIdAndStatus(accountId, status);

        return statements.stream()
                .map(statement -> {
                    List<Transaction> transactions = transactionRepository.findByAccountIdAndDateBetween(
                            accountId,
                            statement.getStartDate(),
                            statement.getEndDate()
                    );
                    return buildStatementResponse(statement, transactions);
                })
                .toList();
    }

    @Override
    @Transactional
    public BankStatementResponseDTO updateStatus(Long id, StatementStatus status, User user) {
        BankStatement statement = bankStatementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Extrato não encontrado com o ID: " + id));

        if (!statement.getAccount().getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Você não tem permissão para atualizar este extrato.");
        }

        statement.setStatus(status);
        BankStatement updatedStatement = bankStatementRepository.save(statement);

        List<Transaction> transactions = transactionRepository.findByAccountIdAndDateBetween(
                statement.getAccount().getId(),
                statement.getStartDate(),
                statement.getEndDate()
        );

        return buildStatementResponse(updatedStatement, transactions);
    }

    @Override
    @Transactional
    public void delete(Long id, User user) {
        BankStatement statement = bankStatementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Extrato não encontrado com o ID: " + id));

        if (!statement.getAccount().getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Você não tem permissão para excluir este extrato.");
        }

        bankStatementRepository.deleteById(id);
    }

    private BankStatementResponseDTO buildStatementResponse(BankStatement statement, List<Transaction> transactions) {
        List<TransactionResponseDTO> transactionDTOs = transactionMapper.toTransactionListResponse(transactions);

        BigDecimal totalIncoming = BigDecimal.ZERO;
        BigDecimal totalOutgoing = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getDestinationAccount().getId().equals(statement.getAccount().getId())) {
                totalIncoming = totalIncoming.add(transaction.getAmount());
            }
            if (transaction.getOriginAccount().getId().equals(statement.getAccount().getId())) {
                totalOutgoing = totalOutgoing.add(transaction.getAmount());
            }
        }

        return new BankStatementResponseDTO(
                statement.getId(),
                statement.getAccount().getId(),
                statement.getAccount().getAgency(),
                statement.getAccount().getAccountNumber(),
                statement.getStartDate(),
                statement.getEndDate(),
                statement.getType(),
                statement.getStatus(),
                statement.getGeneratedAt(),
                totalIncoming,
                totalOutgoing,
                statement.getAccount().getBalance(),
                transactionDTOs
        );
    }
}


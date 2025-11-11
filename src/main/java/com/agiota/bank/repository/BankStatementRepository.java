package com.agiota.bank.repository;

import com.agiota.bank.model.statement.BankStatement;
import com.agiota.bank.model.statement.StatementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BankStatementRepository extends JpaRepository<BankStatement, Long> {
    List<BankStatement> findByAccountId(Long accountId);
    List<BankStatement> findByAccountIdAndStatus(Long accountId, StatementStatus status);
    List<BankStatement> findByAccountIdAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
            Long accountId, LocalDateTime startDate, LocalDateTime endDate);
}


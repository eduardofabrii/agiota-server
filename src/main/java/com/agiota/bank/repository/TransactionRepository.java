package com.agiota.bank.repository;

import com.agiota.bank.model.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByOriginAccountId(Long userId);
    List<Transaction> findByDestinationAccountId(Long userId);

    @Query("SELECT t FROM Transaction t WHERE (t.originAccount.id = :accountId OR t.destinationAccount.id = :accountId) " +
           "AND t.date BETWEEN :startDate AND :endDate ORDER BY t.date DESC")
    List<Transaction> findByAccountIdAndDateBetween(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}

package com.agiota.bank.repository;

import com.agiota.bank.model.loan.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByAccountId(Long accountId);
    List<Loan> findByAccountIdIn(List<Long> accountIds);
}

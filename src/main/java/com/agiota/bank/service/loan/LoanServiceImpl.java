package com.agiota.bank.service.loan;

import com.agiota.bank.dto.request.AdminLoanApprovalRequestDTO;
import com.agiota.bank.dto.request.CreateLoanRequestDTO;
import com.agiota.bank.dto.response.LoanResponseDTO;
import com.agiota.bank.exception.InvalidBankDataException;
import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.mapper.LoanMapper;
import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.loan.Loan;
import com.agiota.bank.model.loan.LoanStatus;
import com.agiota.bank.model.user.User;
import com.agiota.bank.repository.AccountRepository;
import com.agiota.bank.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;
    private final LoanMapper loanMapper;

    @Override
    @Transactional
    public LoanResponseDTO create(CreateLoanRequestDTO request, User user) {
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada com o ID: " + request.accountId()));


        if (!account.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Você não tem permissão para criar um empréstimo para esta conta.");
        }

        Loan loan = loanMapper.toEntity(request, account);
        return loanMapper.toResponse(loanRepository.save(loan));
    }

    @Override
    public List<LoanResponseDTO> findAllByUser(User user) {
        List<Account> userAccounts = accountRepository.findByUserId(user.getId());
        List<Long> accountIds = userAccounts.stream().map(Account::getId).collect(Collectors.toList());
        
        return loanRepository.findByAccountIdIn(accountIds).stream()
                .map(loanMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LoanResponseDTO findById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empréstimo não encontrado com o ID: " + id));
        return loanMapper.toResponse(loan);
    }

    @Override
    @Transactional
    public LoanResponseDTO approveOrRejectLoan(Long id, AdminLoanApprovalRequestDTO request) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empréstimo não encontrado com o ID: " + id));

        if (request.status() == LoanStatus.APPROVED) {
            if (request.interestRate() == null || request.interestRate().compareTo(BigDecimal.ZERO) < 0) {
                throw new InvalidBankDataException("A taxa de juros deve ser fornecida e não pode ser negativa para aprovação.");
            }
            loan.setStatus(LoanStatus.APPROVED);
            loan.setInterestRate(request.interestRate());

            BigDecimal totalAmount = loan.getAmount().multiply(BigDecimal.ONE.add(loan.getInterestRate()));
            BigDecimal installmentValue = totalAmount.divide(new BigDecimal(loan.getInstallments()), 2, RoundingMode.HALF_UP);
            loan.setInstallmentValue(installmentValue);

            Account account = loan.getAccount();
            account.setBalance(account.getBalance().add(loan.getAmount()));
            accountRepository.save(account);

        } else if (request.status() == LoanStatus.REJECTED) {
            loan.setStatus(LoanStatus.REJECTED);
        } else {
            throw new InvalidBankDataException("O status de aprovação deve ser APROVADO ou REJEITADO.");
        }

        return loanMapper.toResponse(loanRepository.save(loan));
    }

    @Override
    @Transactional
    public LoanResponseDTO payInstallment(Long loanId, User user) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Empréstimo não encontrado com o ID: " + loanId));

        if (!loan.getAccount().getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Você não tem permissão para pagar uma parcela deste empréstimo.");
        }

        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new InvalidBankDataException("O empréstimo não está no estado APROVADO.");
        }

        Account account = loan.getAccount();
        if (account.getBalance().compareTo(loan.getInstallmentValue()) < 0) {
            throw new InvalidBankDataException("Saldo insuficiente para pagar a parcela.");
        }

        account.setBalance(account.getBalance().subtract(loan.getInstallmentValue()));
        accountRepository.save(account);

        loan.setPaidInstallments(loan.getPaidInstallments() + 1);
        if (loan.getPaidInstallments().equals(loan.getInstallments())) {
            loan.setStatus(LoanStatus.PAID);
        }

        return loanMapper.toResponse(loanRepository.save(loan));
    }

    @Override
    public void delete(Long id) {
        if (!loanRepository.existsById(id)) {
            throw new ResourceNotFoundException("Empréstimo não encontrado com o ID: " + id);
        }
        loanRepository.deleteById(id);
    }
}

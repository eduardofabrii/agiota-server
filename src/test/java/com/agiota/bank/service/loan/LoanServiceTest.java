package com.agiota.bank.service.loan;

import com.agiota.bank.dto.request.AdminLoanApprovalRequestDTO;
import com.agiota.bank.dto.request.CreateLoanRequestDTO;
import com.agiota.bank.dto.response.LoanResponseDTO;
import com.agiota.bank.exception.InvalidBankDataException;
import com.agiota.bank.mapper.LoanMapper;
import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.loan.Loan;
import com.agiota.bank.model.loan.LoanStatus;
import com.agiota.bank.model.user.User;
import com.agiota.bank.repository.AccountRepository;
import com.agiota.bank.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @InjectMocks
    private LoanServiceImpl loanService;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private LoanMapper loanMapper;

    private User user;
    private Account account;
    private Loan loan;
    private CreateLoanRequestDTO createRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        account = new Account();
        account.setId(1L);
        account.setUser(user);
        account.setBalance(new BigDecimal("1000.00"));

        createRequest = new CreateLoanRequestDTO(account.getId(), new BigDecimal("5000.00"), 12);

        loan = new Loan();
        loan.setId(1L);
        loan.setAccount(account);
        loan.setAmount(new BigDecimal("5000.00"));
        loan.setInstallments(12);
        loan.setStatus(LoanStatus.PENDING);
        loan.setPaidInstallments(0);
    }

    @Test
    void create_shouldCreateLoan_whenAccountBelongsToUser() {
        // Arrange
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(loanMapper.toEntity(any(), any())).thenReturn(loan);
        when(loanRepository.save(any())).thenReturn(loan);

        // Act
        loanService.create(createRequest, user);

        // Assert
        verify(loanRepository).save(loan);
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.PENDING);
    }

    @Test
    void create_shouldThrowException_whenAccountDoesNotBelongToUser() {
        // Arrange
        User anotherUser = new User();
        anotherUser.setId(2L);
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        // Act & Assert
        assertThatThrownBy(() -> loanService.create(createRequest, anotherUser))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void approveOrRejectLoan_shouldApproveLoanAndCreditAccount() {
        // Arrange
        AdminLoanApprovalRequestDTO approvalRequest = new AdminLoanApprovalRequestDTO(LoanStatus.APPROVED, new BigDecimal("0.10"));
        when(loanRepository.findById(loan.getId())).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenReturn(loan);

        // Act
        loanService.approveOrRejectLoan(loan.getId(), approvalRequest);

        // Assert
        verify(accountRepository).save(account);
        verify(loanRepository).save(loan);
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.APPROVED);
        assertThat(loan.getInterestRate()).isEqualByComparingTo("0.10");
        assertThat(loan.getInstallmentValue()).isNotNull();
        assertThat(account.getBalance()).isEqualByComparingTo("6000.00"); // 1000 (initial) + 5000 (loan)
    }

    @Test
    void approveOrRejectLoan_shouldRejectLoan() {
        // Arrange
        AdminLoanApprovalRequestDTO rejectionRequest = new AdminLoanApprovalRequestDTO(LoanStatus.REJECTED, null);
        when(loanRepository.findById(loan.getId())).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenReturn(loan);

        // Act
        loanService.approveOrRejectLoan(loan.getId(), rejectionRequest);

        // Assert
        verify(accountRepository, never()).save(any());
        verify(loanRepository).save(loan);
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.REJECTED);
        assertThat(account.getBalance()).isEqualByComparingTo("1000.00"); // Saldo inalterado
    }

    @Test
    void payInstallment_shouldDebitAccountAndIncrementPaidInstallments() {
        // Arrange
        loan.setStatus(LoanStatus.APPROVED);
        loan.setInstallmentValue(new BigDecimal("458.33"));
        account.setBalance(new BigDecimal("1000.00"));
        when(loanRepository.findById(loan.getId())).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenReturn(loan);

        // Act
        loanService.payInstallment(loan.getId(), user);

        // Assert
        verify(accountRepository).save(account);
        verify(loanRepository).save(loan);
        assertThat(loan.getPaidInstallments()).isEqualTo(1);
        assertThat(account.getBalance()).isEqualByComparingTo("541.67"); // 1000 - 458.33
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.APPROVED);
    }

    @Test
    void payInstallment_shouldChangeStatusToPaid_onLastInstallment() {
        // Arrange
        loan.setStatus(LoanStatus.APPROVED);
        loan.setInstallmentValue(new BigDecimal("458.33"));
        loan.setPaidInstallments(11); // Penúltima parcela
        account.setBalance(new BigDecimal("1000.00"));
        when(loanRepository.findById(loan.getId())).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenReturn(loan);

        // Act
        loanService.payInstallment(loan.getId(), user);

        // Assert
        assertThat(loan.getPaidInstallments()).isEqualTo(12);
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.PAID);
    }

    @Test
    void payInstallment_shouldThrowException_whenBalanceIsInsufficient() {
        // Arrange
        loan.setStatus(LoanStatus.APPROVED);
        loan.setInstallmentValue(new BigDecimal("458.33"));
        account.setBalance(new BigDecimal("100.00")); // Saldo insuficiente
        when(loanRepository.findById(loan.getId())).thenReturn(Optional.of(loan));

        // Act & Assert
        assertThatThrownBy(() -> loanService.payInstallment(loan.getId(), user))
                .isInstanceOf(InvalidBankDataException.class)
                .hasMessage("Saldo insuficiente para pagar a parcela.");
    }
}

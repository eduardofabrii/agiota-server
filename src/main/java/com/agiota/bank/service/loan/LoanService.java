package com.agiota.bank.service.loan;

import com.agiota.bank.dto.request.AdminLoanApprovalRequestDTO;
import com.agiota.bank.dto.request.CreateLoanRequestDTO;
import com.agiota.bank.dto.response.LoanResponseDTO;
import com.agiota.bank.model.user.User;

import java.util.List;

public interface LoanService {
    LoanResponseDTO create(CreateLoanRequestDTO request, User user);
    List<LoanResponseDTO> findAllByUser(User user);
    LoanResponseDTO findById(Long id);
    LoanResponseDTO approveOrRejectLoan(Long id, AdminLoanApprovalRequestDTO request);
    LoanResponseDTO payInstallment(Long loanId, User user);
    void delete(Long id);
}

package com.agiota.bank.controller;

import com.agiota.bank.dto.request.AdminLoanApprovalRequestDTO;
import com.agiota.bank.dto.request.CreateLoanRequestDTO;
import com.agiota.bank.dto.response.LoanResponseDTO;
import com.agiota.bank.model.user.User;
import com.agiota.bank.service.loan.LoanService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/loans")
@RequiredArgsConstructor
@Tag(name = "Empréstimos", description = "Operações relacionadas aos empréstimos")
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanResponseDTO> create(@Valid @RequestBody CreateLoanRequestDTO request, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.create(request, user));
    }

    @GetMapping
    public ResponseEntity<List<LoanResponseDTO>> findAllByUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(loanService.findAllByUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.findById(id));
    }

    @PutMapping("/{id}/approval")
    public ResponseEntity<LoanResponseDTO> approveOrRejectLoan(@PathVariable Long id, @Valid @RequestBody AdminLoanApprovalRequestDTO request) {
        return ResponseEntity.ok(loanService.approveOrRejectLoan(id, request));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<LoanResponseDTO> payInstallment(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(loanService.payInstallment(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        loanService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

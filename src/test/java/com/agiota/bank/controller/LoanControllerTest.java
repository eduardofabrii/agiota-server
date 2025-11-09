package com.agiota.bank.controller;

import com.agiota.bank.dto.request.AdminLoanApprovalRequestDTO;
import com.agiota.bank.dto.request.CreateLoanRequestDTO;
import com.agiota.bank.dto.response.LoanResponseDTO;
import com.agiota.bank.model.loan.LoanStatus;
import com.agiota.bank.model.user.User;
import com.agiota.bank.service.loan.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

    @Mock
    private LoanService loanService;

    @InjectMocks
    private LoanController loanController;

    private User mockUser;
    private CreateLoanRequestDTO createRequest;
    private AdminLoanApprovalRequestDTO approvalRequest;
    private LoanResponseDTO responseDTO;
    private Long loanId = 1L;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);

        createRequest = new CreateLoanRequestDTO(1L, new BigDecimal("5000"), 12);
        approvalRequest = new AdminLoanApprovalRequestDTO(LoanStatus.APPROVED, new BigDecimal("0.1"));

        responseDTO = new LoanResponseDTO(
                loanId,
                1L,
                new BigDecimal("5000"),
                new BigDecimal("0.1"),
                12,
                0,
                new BigDecimal("458.33"),
                LoanStatus.APPROVED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void create_shouldCallServiceAndReturnCreated() {
        // Arrange
        when(loanService.create(any(), any())).thenReturn(responseDTO);

        // Act
        ResponseEntity<LoanResponseDTO> response = loanController.create(createRequest, mockUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(responseDTO);
        verify(loanService).create(eq(createRequest), eq(mockUser));
    }

    @Test
    void findAllByUser_shouldCallServiceAndReturnOk() {
        // Arrange
        List<LoanResponseDTO> loans = Collections.singletonList(responseDTO);
        when(loanService.findAllByUser(any())).thenReturn(loans);

        // Act
        ResponseEntity<List<LoanResponseDTO>> response = loanController.findAllByUser(mockUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(loans);
        verify(loanService).findAllByUser(mockUser);
    }

    @Test
    void findById_shouldCallServiceAndReturnOk() {
        // Arrange
        when(loanService.findById(loanId)).thenReturn(responseDTO);

        // Act
        ResponseEntity<LoanResponseDTO> response = loanController.findById(loanId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDTO);
        verify(loanService).findById(loanId);
    }

    @Test
    void approveOrRejectLoan_shouldCallServiceAndReturnOk() {
        // Arrange
        when(loanService.approveOrRejectLoan(any(), any())).thenReturn(responseDTO);

        // Act
        ResponseEntity<LoanResponseDTO> response = loanController.approveOrRejectLoan(loanId, approvalRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDTO);
        verify(loanService).approveOrRejectLoan(eq(loanId), eq(approvalRequest));
    }

    @Test
    void payInstallment_shouldCallServiceAndReturnOk() {
        // Arrange
        when(loanService.payInstallment(any(), any())).thenReturn(responseDTO);

        // Act
        ResponseEntity<LoanResponseDTO> response = loanController.payInstallment(loanId, mockUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDTO);
        verify(loanService).payInstallment(eq(loanId), eq(mockUser));
    }

    @Test
    void delete_shouldCallServiceAndReturnNoContent() {
        // Arrange
        doNothing().when(loanService).delete(loanId);

        // Act
        ResponseEntity<Void> response = loanController.delete(loanId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(loanService).delete(loanId);
    }
}

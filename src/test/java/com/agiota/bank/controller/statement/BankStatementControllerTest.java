package com.agiota.bank.controller.statement;

import com.agiota.bank.controller.BankStatementController;
import com.agiota.bank.dto.request.BankStatementRequestDTO;
import com.agiota.bank.dto.response.BankStatementResponseDTO;
import com.agiota.bank.model.statement.StatementStatus;
import com.agiota.bank.model.statement.StatementType;
import com.agiota.bank.model.user.User;
import com.agiota.bank.service.statement.BankStatementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BankStatementControllerTest {

    @InjectMocks
    private BankStatementController bankStatementController;

    @Mock
    private BankStatementService bankStatementService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private BankStatementRequestDTO statementRequest;
    private BankStatementResponseDTO statementResponse;
    private User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bankStatementController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        user = new User();
        user.setId(1L);

        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        statementRequest = new BankStatementRequestDTO(
                1L,
                startDate,
                endDate,
                StatementType.MENSAL
        );

        statementResponse = new BankStatementResponseDTO(
                1L,
                1L,
                "0001",
                "12345-6",
                startDate,
                endDate,
                StatementType.MENSAL,
                StatementStatus.GERADO,
                LocalDateTime.now(),
                new BigDecimal("500.00"),
                new BigDecimal("200.00"),
                new BigDecimal("2000.00"),
                List.of()
        );
    }

    @Test
    void generate_ShouldReturnCreated_WhenStatementGeneratedSuccessfully() throws Exception {
        // Arrange
        when(bankStatementService.generate(any(BankStatementRequestDTO.class), any(User.class)))
                .thenReturn(statementResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/statements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statementRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.accountId").value(1L))
                .andExpect(jsonPath("$.type").value("MENSAL"))
                .andExpect(jsonPath("$.status").value("GERADO"));

        verify(bankStatementService, times(1)).generate(any(BankStatementRequestDTO.class), any());
    }

    @Test
    void getById_ShouldReturnStatement_WhenStatementExists() throws Exception {
        // Arrange
        when(bankStatementService.getById(eq(1L), any(User.class))).thenReturn(statementResponse);

        // Act & Assert
        mockMvc.perform(get("/v1/statements/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.accountId").value(1L));

        verify(bankStatementService, times(1)).getById(eq(1L), any());
    }

    @Test
    void getByAccountId_ShouldReturnStatementsList() throws Exception {
        // Arrange
        when(bankStatementService.getByAccountId(eq(1L), any(User.class)))
                .thenReturn(List.of(statementResponse));

        // Act & Assert
        mockMvc.perform(get("/v1/statements/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].accountId").value(1L));

        verify(bankStatementService, times(1)).getByAccountId(eq(1L), any());
    }

    @Test
    void getByAccountIdAndStatus_ShouldReturnFilteredStatements() throws Exception {
        // Arrange
        when(bankStatementService.getByAccountIdAndStatus(eq(1L), eq(StatementStatus.GERADO), any(User.class)))
                .thenReturn(List.of(statementResponse));

        // Act & Assert
        mockMvc.perform(get("/v1/statements/account/1/status/GERADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("GERADO"));

        verify(bankStatementService, times(1))
                .getByAccountIdAndStatus(eq(1L), eq(StatementStatus.GERADO), any());
    }

    @Test
    void updateStatus_ShouldReturnUpdatedStatement() throws Exception {
        // Arrange
        BankStatementResponseDTO updatedResponse = new BankStatementResponseDTO(
                1L,
                1L,
                "0001",
                "12345-6",
                statementResponse.startDate(),
                statementResponse.endDate(),
                StatementType.MENSAL,
                StatementStatus.VISUALIZADO,
                LocalDateTime.now(),
                new BigDecimal("500.00"),
                new BigDecimal("200.00"),
                new BigDecimal("2000.00"),
                List.of()
        );

        when(bankStatementService.updateStatus(eq(1L), eq(StatementStatus.VISUALIZADO), any(User.class)))
                .thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(patch("/v1/statements/1/status/VISUALIZADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VISUALIZADO"));

        verify(bankStatementService, times(1))
                .updateStatus(eq(1L), eq(StatementStatus.VISUALIZADO), any());
    }

    @Test
    void delete_ShouldReturnNoContent_WhenStatementDeleted() throws Exception {
        // Arrange
        doNothing().when(bankStatementService).delete(eq(1L), any(User.class));

        // Act & Assert
        mockMvc.perform(delete("/v1/statements/1"))
                .andExpect(status().isNoContent());

        verify(bankStatementService, times(1)).delete(eq(1L), any());
    }
}


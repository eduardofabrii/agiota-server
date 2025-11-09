package com.agiota.bank.controller;

import com.agiota.bank.dto.request.SupportTicketRequestDTO;
import com.agiota.bank.dto.request.UpdateSupportTicketRequestDTO;
import com.agiota.bank.dto.response.SupportTicketResponseDTO;
import com.agiota.bank.model.supportticket.SupportTicketStatus;
import com.agiota.bank.model.user.User;
import com.agiota.bank.service.supportticket.SupportTicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupportTicketControllerTest {

    @Mock
    private SupportTicketService supportTicketService;

    @InjectMocks
    private SupportTicketController supportTicketController;

    private User mockUser;
    private SupportTicketRequestDTO createRequest;
    private UpdateSupportTicketRequestDTO updateRequest;
    private SupportTicketResponseDTO responseDTO;
    private Long ticketId = 1L;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);

        createRequest = new SupportTicketRequestDTO("Problema", "Descrição");
        updateRequest = new UpdateSupportTicketRequestDTO(SupportTicketStatus.CLOSED, "Resolvido");

        responseDTO = new SupportTicketResponseDTO(
                ticketId,
                mockUser.getId(),
                "Problema",
                "Descrição",
                SupportTicketStatus.OPEN,
                null,
                LocalDateTime.now(),
                null
        );
    }

    @Test
    void create_shouldCreateTicket() {
        // Arrange
        when(supportTicketService.create(any(SupportTicketRequestDTO.class), any(User.class))).thenReturn(responseDTO);

        // Act
        ResponseEntity<SupportTicketResponseDTO> response = supportTicketController.create(createRequest, mockUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(responseDTO);
        verify(supportTicketService).create(eq(createRequest), eq(mockUser));
    }

    @Test
    void findAllByUser_shouldReturnUserTickets() {
        // Arrange
        List<SupportTicketResponseDTO> responses = Collections.singletonList(responseDTO);
        when(supportTicketService.findAllByUser(mockUser)).thenReturn(responses);

        // Act
        ResponseEntity<List<SupportTicketResponseDTO>> response = supportTicketController.findAllByUser(mockUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responses);
        verify(supportTicketService).findAllByUser(mockUser);
    }

    @Test
    void findById_shouldReturnTicket() {
        // Arrange
        when(supportTicketService.findById(ticketId)).thenReturn(responseDTO);

        // Act
        ResponseEntity<SupportTicketResponseDTO> response = supportTicketController.findById(ticketId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDTO);
        verify(supportTicketService).findById(ticketId);
    }

    @Test
    void update_shouldUpdateTicket() {
        // Arrange
        when(supportTicketService.update(eq(ticketId), any(UpdateSupportTicketRequestDTO.class))).thenReturn(responseDTO);

        // Act
        ResponseEntity<SupportTicketResponseDTO> response = supportTicketController.update(ticketId, updateRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDTO);
        verify(supportTicketService).update(eq(ticketId), eq(updateRequest));
    }

    @Test
    void delete_shouldDeleteTicket() {
        // Arrange
        doNothing().when(supportTicketService).delete(ticketId);

        // Act
        ResponseEntity<Void> response = supportTicketController.delete(ticketId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(supportTicketService).delete(ticketId);
    }
}

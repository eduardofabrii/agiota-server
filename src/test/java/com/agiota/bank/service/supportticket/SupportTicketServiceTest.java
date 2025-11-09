package com.agiota.bank.service.supportticket;

import com.agiota.bank.dto.request.SupportTicketRequestDTO;
import com.agiota.bank.dto.request.UpdateSupportTicketRequestDTO;
import com.agiota.bank.dto.response.SupportTicketResponseDTO;
import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.mapper.SupportTicketMapper;
import com.agiota.bank.model.supportticket.SupportTicket;
import com.agiota.bank.model.supportticket.SupportTicketStatus;
import com.agiota.bank.model.user.User;
import com.agiota.bank.repository.SupportTicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupportTicketServiceTest {

    @InjectMocks
    private SupportTicketServiceImpl supportTicketService;

    @Mock
    private SupportTicketRepository supportTicketRepository;

    @Mock
    private SupportTicketMapper supportTicketMapper;

    private User user;
    private SupportTicket supportTicket;
    private SupportTicketRequestDTO createRequest;
    private UpdateSupportTicketRequestDTO updateRequest;
    private SupportTicketResponseDTO responseDTO;
    private Long ticketId = 1L;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        createRequest = new SupportTicketRequestDTO("Problema com login", "Não consigo acessar minha conta.");

        updateRequest = new UpdateSupportTicketRequestDTO(SupportTicketStatus.CLOSED, "Resolvido.");

        supportTicket = new SupportTicket();
        supportTicket.setId(ticketId);
        supportTicket.setUser(user);
        supportTicket.setTitle("Problema com login");
        supportTicket.setDescription("Não consigo acessar minha conta.");
        supportTicket.setStatus(SupportTicketStatus.OPEN);
        supportTicket.setCreatedAt(LocalDateTime.now());

        responseDTO = new SupportTicketResponseDTO(
                ticketId,
                user.getId(),
                "Problema com login",
                "Não consigo acessar minha conta.",
                SupportTicketStatus.OPEN,
                null,
                supportTicket.getCreatedAt(),
                null
        );
    }

    @Test
    void create_shouldCreateTicket() {
        // Arrange
        when(supportTicketMapper.toEntity(createRequest, user)).thenReturn(supportTicket);
        when(supportTicketRepository.save(any(SupportTicket.class))).thenReturn(supportTicket);
        when(supportTicketMapper.toResponse(supportTicket)).thenReturn(responseDTO);

        // Act
        SupportTicketResponseDTO result = supportTicketService.create(createRequest, user);

        // Assert
        assertThat(result).isEqualTo(responseDTO);
        verify(supportTicketRepository).save(supportTicket);
    }

    @Test
    void findAllByUser_shouldReturnUserTickets() {
        // Arrange
        List<SupportTicket> tickets = Collections.singletonList(supportTicket);
        List<SupportTicketResponseDTO> responses = Collections.singletonList(responseDTO);
        when(supportTicketRepository.findByUserId(user.getId())).thenReturn(tickets);
        when(supportTicketMapper.toResponse(any(SupportTicket.class))).thenReturn(responseDTO);

        // Act
        List<SupportTicketResponseDTO> result = supportTicketService.findAllByUser(user);

        // Assert
        assertThat(result).isEqualTo(responses);
        verify(supportTicketRepository).findByUserId(user.getId());
    }

    @Test
    void findById_shouldReturnTicket_whenExists() {
        // Arrange
        when(supportTicketRepository.findById(ticketId)).thenReturn(Optional.of(supportTicket));
        when(supportTicketMapper.toResponse(supportTicket)).thenReturn(responseDTO);

        // Act
        SupportTicketResponseDTO result = supportTicketService.findById(ticketId);

        // Assert
        assertThat(result).isEqualTo(responseDTO);
    }

    @Test
    void findById_shouldThrowException_whenNotExists() {
        // Arrange
        when(supportTicketRepository.findById(ticketId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> supportTicketService.findById(ticketId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_shouldUpdateTicket() {
        // Arrange
        when(supportTicketRepository.findById(ticketId)).thenReturn(Optional.of(supportTicket));
        when(supportTicketRepository.save(any(SupportTicket.class))).thenReturn(supportTicket);
        when(supportTicketMapper.toResponse(supportTicket)).thenReturn(responseDTO);
        
        // Act
        supportTicketService.update(ticketId, updateRequest);

        // Assert
        verify(supportTicketRepository).findById(ticketId);
        verify(supportTicketMapper).updateEntityFromDto(updateRequest, supportTicket);
        verify(supportTicketRepository).save(supportTicket);
    }

    @Test
    void delete_shouldDeleteTicket_whenExists() {
        // Arrange
        when(supportTicketRepository.existsById(ticketId)).thenReturn(true);
        doNothing().when(supportTicketRepository).deleteById(ticketId);

        // Act
        supportTicketService.delete(ticketId);

        // Assert
        verify(supportTicketRepository).deleteById(ticketId);
    }

    @Test
    void delete_shouldThrowException_whenNotExists() {
        // Arrange
        when(supportTicketRepository.existsById(ticketId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> supportTicketService.delete(ticketId))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(supportTicketRepository, never()).deleteById(anyLong());
    }
}

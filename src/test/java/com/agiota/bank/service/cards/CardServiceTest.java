package com.agiota.bank.service.cards;

import com.agiota.bank.dto.request.CardRequestDTO;
import com.agiota.bank.dto.response.CardResponseDTO;
import com.agiota.bank.exception.CardException;
import com.agiota.bank.mapper.CardMapper;
import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.cards.Card;
import com.agiota.bank.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardMapper cardMapper;

    private Card mockCard;
    private CardRequestDTO mockCardRequest;
    private CardResponseDTO mockCardResponse;
    private final Long cardId = 1L;
    private final Long accountId = 1L;

    @BeforeEach
    void setUp() {
        Account mockAccount = new Account();
        mockAccount.setId(accountId);

        mockCard = new Card(
                cardId,
                "1234567812345678",
                "Test Holder",
                "12/25",
                "123",
                mockAccount
        );

        mockCardRequest = new CardRequestDTO(
                "1234567812345678",
                "Test Holder",
                "12/25",
                "123",
                accountId
        );

        mockCardResponse = new CardResponseDTO(
                cardId,
                "1234567812345678",
                "Test Holder",
                "12/25",
                accountId
        );
    }

    @Test
    void create_shouldCreateCard() {
        // Arrange
        when(cardMapper.toCardPostRequest(any(CardRequestDTO.class))).thenReturn(mockCard);
        when(cardRepository.save(any(Card.class))).thenReturn(mockCard);
        when(cardMapper.toCardPostResponse(any(Card.class))).thenReturn(mockCardResponse);

        // Act
        CardResponseDTO result = cardService.create(mockCardRequest);

        // Assert
        assertThat(result).isEqualTo(mockCardResponse);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void getById_shouldReturnCard_whenCardExists() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(mockCard));
        when(cardMapper.toCardPostResponse(mockCard)).thenReturn(mockCardResponse);

        // Act
        CardResponseDTO result = cardService.getById(cardId);

        // Assert
        assertThat(result).isEqualTo(mockCardResponse);
        verify(cardRepository).findById(cardId);
    }

    @Test
    void getById_shouldThrowException_whenCardNotExists() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cardService.getById(cardId))
                .isInstanceOf(CardException.class)
                .hasMessageContaining("Cartão não encontrado");

        verify(cardRepository).findById(cardId);
    }

    @Test
    void getAll_shouldReturnListOfCards() {
        // Arrange
        List<Card> cards = Collections.singletonList(mockCard);
        List<CardResponseDTO> cardResponses = Collections.singletonList(mockCardResponse);
        when(cardRepository.findAll()).thenReturn(cards);
        when(cardMapper.toCardListResponse(cards)).thenReturn(cardResponses);

        // Act
        List<CardResponseDTO> result = cardService.getAll();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(mockCardResponse);
        verify(cardRepository).findAll();
    }

    @Test
    void update_shouldUpdateCard_whenCardExists() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(mockCard));
        when(cardRepository.save(any(Card.class))).thenReturn(mockCard);
        when(cardMapper.toCardPostResponse(mockCard)).thenReturn(mockCardResponse);

        // Act
        CardResponseDTO result = cardService.update(cardId, mockCardRequest);

        // Assert
        assertThat(result).isEqualTo(mockCardResponse);
        verify(cardRepository).findById(cardId);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void update_shouldThrowException_whenCardNotExists() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cardService.update(cardId, mockCardRequest))
                .isInstanceOf(CardException.class)
                .hasMessageContaining("Cartão não encontrado");

        verify(cardRepository).findById(cardId);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteCard() {
        // Arrange
        when(cardRepository.existsById(cardId)).thenReturn(true);
        doNothing().when(cardRepository).deleteById(cardId);

        // Act
        cardService.delete(cardId);

        // Assert
        verify(cardRepository).existsById(cardId);
        verify(cardRepository).deleteById(cardId);
    }
}
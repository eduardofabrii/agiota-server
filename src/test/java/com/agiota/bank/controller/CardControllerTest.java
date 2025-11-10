package com.agiota.bank.controller;

import com.agiota.bank.dto.request.CardRequestDTO;
import com.agiota.bank.dto.response.CardResponseDTO;
import com.agiota.bank.service.cards.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    private CardRequestDTO mockCardRequest;
    private CardResponseDTO mockCardResponse;
    private Long cardId = 1L;
    private Long accountId = 1L;

    @BeforeEach
    void setUp() {
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

        // Mock Spring's RequestContextHolder for URI creation in create()
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/v1/cards");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void create_shouldReturnCreated() {
        when(cardService.create(any(CardRequestDTO.class))).thenReturn(mockCardResponse);

        ResponseEntity<CardResponseDTO> response = cardController.create(mockCardRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockCardResponse, response.getBody());
        assertEquals("/v1/cards/1", Objects.requireNonNull(response.getHeaders().getLocation()).getPath());
    }

    @Test
    void getById_shouldReturnCard() {
        when(cardService.getById(cardId)).thenReturn(mockCardResponse);

        ResponseEntity<CardResponseDTO> response = cardController.getById(cardId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCardResponse, response.getBody());
    }

    @Test
    void getAll_shouldReturnListOfCards() {
        when(cardService.getAll()).thenReturn(Collections.singletonList(mockCardResponse));

        ResponseEntity<List<CardResponseDTO>> response = cardController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        assertEquals(mockCardResponse, response.getBody().get(0));
    }

    @Test
    void update_shouldReturnUpdatedCard() {
        when(cardService.update(eq(cardId), any(CardRequestDTO.class))).thenReturn(mockCardResponse);

        ResponseEntity<CardResponseDTO> response = cardController.update(cardId, mockCardRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCardResponse, response.getBody());
    }

    @Test
    void delete_shouldReturnNoContent() {
        doNothing().when(cardService).delete(cardId);

        ResponseEntity<Void> response = cardController.delete(cardId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
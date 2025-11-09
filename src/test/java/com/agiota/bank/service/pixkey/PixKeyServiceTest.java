package com.agiota.bank.service.pixkey;

import com.agiota.bank.dto.request.PixKeyRequestDTO;
import com.agiota.bank.dto.response.PixKeyResponseDTO;
import com.agiota.bank.exception.ResourceAlreadyExistsException;
import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.mapper.PixKeyMapper;
import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.pixkey.PixKey;
import com.agiota.bank.model.pixkey.PixKeyTypes;
import com.agiota.bank.repository.PixKeyRepository;
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
class PixKeyServiceTest {

    @InjectMocks
    private PixKeyServiceImpl pixKeyService;

    @Mock
    private PixKeyRepository pixKeyRepository;

    @Mock
    private PixKeyMapper mapper;

    private PixKeyRequestDTO requestDTO;
    private PixKeyResponseDTO responseDTO;
    private PixKey pixKey;
    private Account account;
    private String keyValue = "test@test.com";
    private Long accountId = 1L;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(accountId);

        requestDTO = new PixKeyRequestDTO(keyValue, PixKeyTypes.EMAIL);

        pixKey = new PixKey();
        pixKey.setKeyValue(keyValue);
        pixKey.setType(PixKeyTypes.EMAIL);
        pixKey.setAccount(account);

        responseDTO = new PixKeyResponseDTO(keyValue, PixKeyTypes.EMAIL.name(), accountId);
    }

    @Test
    void createPixKey_shouldCreateKey_whenKeyDoesNotExist() {
        // Arrange
        when(pixKeyRepository.existsById(keyValue)).thenReturn(false);
        when(mapper.toPixKeyPostRequest(requestDTO, accountId)).thenReturn(pixKey);
        when(pixKeyRepository.save(any(PixKey.class))).thenReturn(pixKey);
        when(mapper.toPixKeyPostResponse(pixKey)).thenReturn(responseDTO);

        // Act
        PixKeyResponseDTO result = pixKeyService.createPixKey(requestDTO, accountId);

        // Assert
        assertThat(result).isEqualTo(responseDTO);
        verify(pixKeyRepository).existsById(keyValue);
        verify(pixKeyRepository).save(pixKey);
    }

    @Test
    void createPixKey_shouldThrowException_whenKeyAlreadyExists() {
        // Arrange
        when(pixKeyRepository.existsById(keyValue)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> pixKeyService.createPixKey(requestDTO, accountId))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("Pix key '" + keyValue + "' already exists.");

        verify(pixKeyRepository, never()).save(any());
    }

    @Test
    void getPixKey_shouldReturnKey_whenKeyExists() {
        // Arrange
        when(pixKeyRepository.findByKeyValue(keyValue)).thenReturn(Optional.of(pixKey));
        when(mapper.toPixKeyPostResponse(pixKey)).thenReturn(responseDTO);

        // Act
        PixKeyResponseDTO result = pixKeyService.getPixKey(keyValue);

        // Assert
        assertThat(result).isEqualTo(responseDTO);
        verify(pixKeyRepository).findByKeyValue(keyValue);
    }

    @Test
    void getPixKey_shouldThrowException_whenKeyDoesNotExist() {
        // Arrange
        when(pixKeyRepository.findByKeyValue(keyValue)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pixKeyService.getPixKey(keyValue))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("PixKey not found");
    }

    @Test
    void listPixKeyByAccountId_shouldReturnKeys() {
        // Arrange
        List<PixKey> keys = Collections.singletonList(pixKey);
        List<PixKeyResponseDTO> responses = Collections.singletonList(responseDTO);
        when(pixKeyRepository.findByAccountId(accountId)).thenReturn(keys);
        when(mapper.toPixKeyListResponse(keys)).thenReturn(responses);

        // Act
        List<PixKeyResponseDTO> result = pixKeyService.listPixKeyByAccountId(accountId);

        // Assert
        assertThat(result).isEqualTo(responses);
        verify(pixKeyRepository).findByAccountId(accountId);
    }

    @Test
    void deletePixKey_shouldDeleteKey_whenKeyExists() {
        // Arrange
        when(pixKeyRepository.existsById(keyValue)).thenReturn(true);
        doNothing().when(pixKeyRepository).deleteById(keyValue);

        // Act
        pixKeyService.deletePixKey(keyValue);

        // Assert
        verify(pixKeyRepository).existsById(keyValue);
        verify(pixKeyRepository).deleteById(keyValue);
    }

    @Test
    void deletePixKey_shouldThrowException_whenKeyDoesNotExist() {
        // Arrange
        when(pixKeyRepository.existsById(keyValue)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> pixKeyService.deletePixKey(keyValue))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("PixKey not found");

        verify(pixKeyRepository, never()).deleteById(anyString());
    }
}

package com.agiota.bank.service.pixkey;

import com.agiota.bank.dto.request.PixKeyRequestDTO;
import com.agiota.bank.dto.response.PixKeyResponseDTO;
import com.agiota.bank.exception.AcessDeniedException;
import com.agiota.bank.exception.InvalidRequestException;
import com.agiota.bank.mapper.PixKeyMapper;
import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.pixkey.PixKey;
import com.agiota.bank.model.pixkey.PixKeyTypes;
import com.agiota.bank.model.user.User;
import com.agiota.bank.repository.AccountRepository;
import com.agiota.bank.repository.PixKeyRepository;
import com.agiota.bank.service.notification.NotificationService;
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
    private AccountRepository accountRepository;

    @Mock
    private PixKeyMapper mapper;

    @Mock
    private NotificationService notificationService;

    private User user;
    private Account account;
    private PixKeyRequestDTO requestDTO;
    private PixKeyResponseDTO responseDTO;
    private PixKey pixKey;
    private String keyValue = "test@test.com";
    private Long accountId = 1L;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        account = new Account();
        account.setId(accountId);
        account.setUser(user);

        requestDTO = new PixKeyRequestDTO(keyValue, PixKeyTypes.EMAIL);

        pixKey = new PixKey();
        pixKey.setKeyValue(keyValue);
        pixKey.setType(PixKeyTypes.EMAIL);
        pixKey.setAccount(account);

        responseDTO = new PixKeyResponseDTO(keyValue, PixKeyTypes.EMAIL.name(), accountId);
    }

    @Test
    void createPixKey_shouldCreateKey_whenDataIsValidAndUserHasPermission() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(pixKeyRepository.existsById(keyValue)).thenReturn(false);
        when(mapper.toPixKeyPostRequest(requestDTO, accountId)).thenReturn(pixKey);
        when(pixKeyRepository.save(any(PixKey.class))).thenReturn(pixKey);
        when(mapper.toPixKeyPostResponse(pixKey)).thenReturn(responseDTO);

        // Act
        PixKeyResponseDTO result = pixKeyService.createPixKey(requestDTO, accountId, user);

        // Assert
        assertThat(result).isEqualTo(responseDTO);
        verify(pixKeyRepository).save(pixKey);
    }

    @Test
    void createPixKey_shouldThrowException_whenUserHasNoPermission() {
        // Arrange
        User anotherUser = new User();
        anotherUser.setId(2L);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Act & Assert
        assertThatThrownBy(() -> pixKeyService.createPixKey(requestDTO, accountId, anotherUser))
                .isInstanceOf(AcessDeniedException.class);
    }

    @Test
    void createPixKey_shouldThrowException_whenKeyIsNull() {
        // Arrange
        PixKeyRequestDTO invalidRequest = new PixKeyRequestDTO(null, PixKeyTypes.EMAIL);

        // Act & Assert
        assertThatThrownBy(() -> pixKeyService.createPixKey(invalidRequest, accountId, user))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("O valor da chave PIX não pode ser nulo ou vazio.");
    }

    @Test
    void listPixKeyByAccountId_shouldReturnKeys() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(pixKeyRepository.findByAccountId(accountId)).thenReturn(Collections.singletonList(pixKey));
        when(mapper.toPixKeyListResponse(any())).thenReturn(Collections.singletonList(responseDTO));

        // Act
        List<PixKeyResponseDTO> result = pixKeyService.listPixKeyByAccountId(accountId);

        // Assert
        assertThat(result).hasSize(1);
        verify(pixKeyRepository).findByAccountId(accountId);
    }

    @Test
    void deletePixKey_shouldDeleteKey_whenUserHasPermission() {
        // Arrange
        when(pixKeyRepository.findByKeyValue(keyValue)).thenReturn(Optional.of(pixKey));
        doNothing().when(pixKeyRepository).deleteById(keyValue);

        // Act
        pixKeyService.deletePixKey(keyValue, user);

        // Assert
        verify(pixKeyRepository).deleteById(keyValue);
    }

    @Test
    void deletePixKey_shouldThrowException_whenUserHasNoPermission() {
        // Arrange
        User anotherUser = new User();
        anotherUser.setId(2L);
        when(pixKeyRepository.findByKeyValue(keyValue)).thenReturn(Optional.of(pixKey));

        // Act & Assert
        assertThatThrownBy(() -> pixKeyService.deletePixKey(keyValue, anotherUser))
                .isInstanceOf(AcessDeniedException.class);
    }
}

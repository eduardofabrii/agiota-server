package com.agiota.bank.service.user;

import com.agiota.bank.dto.request.UserRequestDTO;
import com.agiota.bank.dto.response.UserResponseDTO;
import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.model.user.User;
import com.agiota.bank.model.user.UserRole;
import com.agiota.bank.repository.UserRepository;
import com.agiota.bank.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private User mockUser;
    private UserRequestDTO mockUserRequest;
    private UserResponseDTO mockUserResponse;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Test User");
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("password123");
        mockUser.setRole(UserRole.USER);
        mockUser.setActive(true);
        mockUser.setCreatedAt(LocalDateTime.now());

        mockUserRequest = new UserRequestDTO(
                "Test User",
                "password123",
                "test@example.com",
                UserRole.USER
        );

        mockUserResponse = new UserResponseDTO(
                1L,
                "Test User",
                "test@example.com",
                LocalDateTime.now(),
                null
        );
    }

    @Test
    void listAll_shouldReturnAllUsers() {
        // Arrange
        List<User> users = Arrays.asList(mockUser);
        List<UserResponseDTO> expectedResponse = Arrays.asList(mockUserResponse);
        
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toUserListResponse(users)).thenReturn(expectedResponse);

        // Act
        List<UserResponseDTO> result = userService.listAll();

        // Assert
        assertThat(result).hasSize(1);
        verify(userRepository).findAll();
        verify(userMapper).toUserListResponse(users);
    }

    @Test
    void create_shouldCreateUser() {
        // Arrange
        when(userMapper.toUserPostRequest(mockUserRequest)).thenReturn(mockUser);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toUserPostResponse(mockUser)).thenReturn(mockUserResponse);

        // Act
        UserResponseDTO result = userService.create(mockUserRequest);

        // Assert
        assertThat(result).isEqualTo(mockUserResponse);
        verify(userMapper).toUserPostRequest(mockUserRequest);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserPostResponse(mockUser);
    }

    @Test
    void listUserById_shouldReturnUser_whenUserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userMapper.toUserPostResponse(mockUser)).thenReturn(mockUserResponse);

        // Act
        UserResponseDTO result = userService.listUserById(1L);

        // Assert
        assertThat(result).isEqualTo(mockUserResponse);
        verify(userRepository).findById(1L);
        verify(userMapper).toUserPostResponse(mockUser);
    }

    @Test
    void listUserById_shouldThrowException_whenUserNotExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.listUserById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
        
        verify(userRepository).findById(1L);
        verify(userMapper, never()).toUserPostResponse(any());
    }

    @Test
    void update_shouldUpdateUser_whenUserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userMapper.toUserPostResponse(mockUser)).thenReturn(mockUserResponse);

        // Act
        UserResponseDTO result = userService.update(1L, mockUserRequest);

        // Assert
        assertThat(result).isEqualTo(mockUserResponse);
        verify(userRepository).findById(1L);
        verify(userRepository).save(mockUser);
        verify(userMapper).toUserPostResponse(mockUser);
    }

    @Test
    void update_shouldThrowException_whenUserNotExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.update(1L, mockUserRequest))
                .isInstanceOf(ResourceNotFoundException.class);
        
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteUser_whenUserExists() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);

        // Act
        userService.delete(1L);

        // Assert
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_shouldThrowException_whenUserNotExists() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> userService.delete(1L))
                .isInstanceOf(ResourceNotFoundException.class);
        
        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void softDelete_shouldDeactivateUser_whenUserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // Act
        userService.softDelete(1L);

        // Assert
        assertThat(mockUser.getActive()).isFalse();
        verify(userRepository).findById(1L);
        verify(userRepository).save(mockUser);
    }

    @Test
    void softDelete_shouldThrowException_whenUserNotExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.softDelete(1L))
                .isInstanceOf(ResourceNotFoundException.class);
        
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void restore_shouldActivateUser_whenUserExists() {
        // Arrange
        mockUser.setActive(false);
        mockUser.setDeletedAt(LocalDateTime.now()); // Simula usuário deletado
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // Act
        userService.restore(1L);

        // Assert
        assertThat(mockUser.getActive()).isTrue();
        assertThat(mockUser.getDeletedAt()).isNull();
        verify(userRepository).findById(1L);
        verify(userRepository).save(mockUser);
    }

    @Test
    void restore_shouldThrowException_whenUserNotExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.restore(1L))
                .isInstanceOf(ResourceNotFoundException.class);
        
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateLastLogin_shouldUpdateUser_whenUserExists() {
        // Arrange
        when(userRepository.findByUsername("Test User")).thenReturn(Optional.of(mockUser));

        // Act
        userService.updateLastLogin("Test User");

        // Assert
        assertThat(mockUser.getLastLogin()).isNotNull();
        verify(userRepository).findByUsername("Test User");
        verify(userRepository).save(mockUser);
    }

    @Test
    void updateLastLogin_shouldThrowException_whenUserNotExists() {
        // Arrange
        when(userRepository.findByUsername("Test User")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.updateLastLogin("Test User"))
                .isInstanceOf(ResourceNotFoundException.class);
        
        verify(userRepository).findByUsername("Test User");
        verify(userRepository, never()).save(any());
    }
}
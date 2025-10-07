package com.agiota.bank.service.user;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.agiota.bank.dto.request.UserRequestDTO;
import com.agiota.bank.dto.response.UserResponseDTO;
import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.mapper.UserMapper;
import com.agiota.bank.model.user.User;
import com.agiota.bank.repository.UserRepository;

import lombok.AllArgsConstructor;


@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public List<UserResponseDTO> listAll() {
        return mapper.toUserListResponse(userRepository.findAll());
    }

    @Override
    public UserResponseDTO create(UserRequestDTO postRequest) {
        User user = mapper.toUserPostRequest(postRequest);
        userRepository.save(user);
        return mapper.toUserPostResponse(user);
    }

    @Override
    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.save(user);
        return mapper.toUserPostResponse(user);
    }

    @Override
    public UserResponseDTO listUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapper.toUserPostResponse(user);
    }

    public void softDelete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setDeletedAt(LocalDateTime.now());
        user.setActive(false);
        userRepository.save(user);
    }

    public void restore(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getDeletedAt() != null) {
            user.setDeletedAt(null);
            user.setActive(true);
            userRepository.save(user);
        } else {
            throw new IllegalStateException("User is not deleted");
        }
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    public void updateLastLogin(String name) {
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }
}

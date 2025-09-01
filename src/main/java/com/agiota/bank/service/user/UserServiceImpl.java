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
    public void updateLastLogin(String name) {
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }
}

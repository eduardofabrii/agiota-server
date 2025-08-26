package com.agiota.bank.service.user;

import java.util.List;

import org.springframework.stereotype.Service;

import com.agiota.bank.dto.request.UserRequestDTO;
import com.agiota.bank.dto.response.UserResponseDTO;
import com.agiota.bank.mapper.UserMapper;
import com.agiota.bank.model.user.User;
import com.agiota.bank.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private UserMapper mapper;

    @Override
    public UserResponseDTO create(UserRequestDTO postRequest) {
        User user = mapper.toUserPost(postRequest);
        userRepository.save(user);
        return mapper.toUserPostResponse(user);
    }
}

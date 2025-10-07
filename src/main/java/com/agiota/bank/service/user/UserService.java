package com.agiota.bank.service.user;

import java.util.List;

import com.agiota.bank.dto.request.UserRequestDTO;
import com.agiota.bank.dto.response.UserResponseDTO;

public interface UserService {
    List<UserResponseDTO> listAll();
    UserResponseDTO create(UserRequestDTO postRequest);
    UserResponseDTO listUserById(Long id);
    UserResponseDTO update(Long id, UserRequestDTO dto);
    void delete(Long id);
    void updateLastLogin(String name);
}

package com.agiota.bank.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.agiota.bank.dto.request.UserRequestDTO;
import com.agiota.bank.dto.response.UserResponseDTO;
import com.agiota.bank.model.user.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User toUserPostRequest(UserRequestDTO userRequest);

    UserResponseDTO toUserPostResponse(User user);

    List<UserResponseDTO> toUserListResponse(List<User> users);
}
package com.agiota.bank.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.agiota.bank.dto.request.UserRequestDTO;
import com.agiota.bank.dto.response.UserResponseDTO;
import com.agiota.bank.model.user.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserResponseDTO toUserGetResponse(UserResponseDTO user);

    @Mapping(target = "id", ignore = true)
    User toUserPost(UserRequestDTO userRequest);

    UserResponseDTO toUserPostResponse(User user);

    List<UserResponseDTO> toUserListResponse(List<User> users);
}
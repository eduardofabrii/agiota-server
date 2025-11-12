package com.agiota.bank.mapper;

import com.agiota.bank.dto.request.AuthorizedDeviceRequestDTO;
import com.agiota.bank.dto.response.AuthorizedDeviceResponseDTO;
import com.agiota.bank.model.device.AuthorizedDevice;
import com.agiota.bank.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorizedDeviceMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "lastLoginDate", expression = "java(java.time.LocalDateTime.now())"),
        @Mapping(target = "authorized", constant = "true"),
        @Mapping(target = "user", source = "user")
    })
    AuthorizedDevice toEntity(AuthorizedDeviceRequestDTO dto, User user);

    @Mappings({
        @Mapping(target = "userId", source = "user.id"),
        @Mapping(target = "isAuthorized", source = "authorized")
    })
    AuthorizedDeviceResponseDTO toResponse(AuthorizedDevice entity);

    List<AuthorizedDeviceResponseDTO> toResponseList(List<AuthorizedDevice> entities);
}

package com.agiota.bank.mapper;


import com.agiota.bank.dto.request.PixKeyRequestDTO;
import com.agiota.bank.dto.response.PixKeyResponseDTO;
import com.agiota.bank.model.pixkey.PixKey;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PixKeyMapper {
    PixKey toPixKeyPostRequest(PixKeyRequestDTO userRequest, Long ownerId);

    PixKeyResponseDTO toPixKeyPostResponse(PixKey pixKey);

    List<PixKeyResponseDTO> toPixKeyListResponse(List<PixKey> pixKeys);
}
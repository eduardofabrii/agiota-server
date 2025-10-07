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

    @Mapping(target = "id", ignore = true)

    PixKey toPixKeyPostRequest(PixKeyRequestDTO userRequest, Long ownerId);

    PixKeyResponseDTO toPixKeyResponse(PixKey pixKey);

    List<PixKeyResponseDTO> toPixKeyResponseDTOList(List<PixKey> pixKeys);
}
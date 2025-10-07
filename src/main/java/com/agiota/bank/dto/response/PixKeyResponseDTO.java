package com.agiota.bank.dto.response;

public record PixKeyResponseDTO(
        String keyValue,
        String keyType,
        Long ownerId
) {

}

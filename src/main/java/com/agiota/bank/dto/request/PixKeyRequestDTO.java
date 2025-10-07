package com.agiota.bank.dto.request;

import com.agiota.bank.model.pixkey.PixKeyTypes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PixKeyRequestDTO(
        @NotBlank @NotNull String keyValue,
        @NotNull PixKeyTypes keyType
) {

}

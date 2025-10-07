package com.agiota.bank.dto.response;

import lombok.Data;

@Data
public record CardResponseDTO {
    private Long id;
    private String number;
    private String holderName;
    private String expirationDate;
}

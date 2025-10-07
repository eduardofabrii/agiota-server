package com.agiota.bank.dto.request;

import lombok.Data;

@Data
public record CardRequestDTO {
    private String number;
    private String holderName;
    private String expirationDate;
    private String cvv;
}

package com.agiota.bank.dto.request;

public record CardRequestDTO(String number,
                             String holderName,
                             String expirationDate,
                             String cvv) {
}

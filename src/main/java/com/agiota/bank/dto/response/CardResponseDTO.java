package com.agiota.bank.dto.response;

public record CardResponseDTO(Long id,
                              String number,
                              String holderName,
                              String expirationDate) {
}

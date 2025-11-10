package com.agiota.bank.exception;

import lombok.Getter;

@Getter
public class CardException extends RuntimeException {

    private final ErrorCode errorCode;

    public CardException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
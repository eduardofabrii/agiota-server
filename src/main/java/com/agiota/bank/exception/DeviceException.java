package com.agiota.bank.exception;

import lombok.Getter;

@Getter
public class DeviceException extends RuntimeException {

    private final ErrorCode errorCode;

    public DeviceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

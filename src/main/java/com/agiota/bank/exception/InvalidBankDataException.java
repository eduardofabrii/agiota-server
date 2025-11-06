package com.agiota.bank.exception;

public class InvalidBankDataException extends RuntimeException {
    
    public InvalidBankDataException(String message) {
        super(message);
    }
    
    public InvalidBankDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
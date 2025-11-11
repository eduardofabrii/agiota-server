package com.agiota.bank.exception;

public class StatementException extends CustomException {
    public StatementException(String message, int statusCode) {
        super(message, statusCode);
    }

    public StatementException(String message) {
        super(message, 400);
    }
}


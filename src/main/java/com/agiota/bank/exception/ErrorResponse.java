package com.agiota.bank.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private String details;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime timestamp;
    private String errorCode;

    public ErrorResponse(int status, String message, String details) {
        this.status = status;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
        this.errorCode = null;
    }

    public ErrorResponse(int status, String message, String details, String errorCode) {
        this.status = status;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
        this.errorCode = errorCode;
    }
}
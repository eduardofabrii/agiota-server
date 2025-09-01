package com.agiota.bank.exception;

public class ResourceNotFoundException extends CustomException {
    
    public ResourceNotFoundException(String mensagem) {
        super(mensagem, 404);
    }
}
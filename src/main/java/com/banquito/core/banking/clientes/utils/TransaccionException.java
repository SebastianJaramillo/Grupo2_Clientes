package com.banquito.core.banking.clientes.utils;

public class TransaccionException extends RuntimeException {

    public TransaccionException(String message) {
        super(message);
    }

    public TransaccionException(String message, Exception cause) {
        super(message, cause);
    }
}

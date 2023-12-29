package com.banquito.core.banking.clientes.utils;

public class ValidacionException extends RuntimeException {

    public ValidacionException(String message) {
        super("Valor incorrecto en " + message);
    }
}

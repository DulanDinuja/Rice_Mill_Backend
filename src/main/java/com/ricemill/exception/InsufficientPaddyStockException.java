package com.ricemill.exception;

public class InsufficientPaddyStockException extends RuntimeException {
    public InsufficientPaddyStockException(String message) {
        super(message);
    }
}


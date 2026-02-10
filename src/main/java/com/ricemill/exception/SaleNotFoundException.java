package com.ricemill.exception;

public class SaleNotFoundException extends RuntimeException {
    public SaleNotFoundException(String message) {
        super(message);
    }

    public SaleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}


package com.ricemill.exception;

public class InvalidSaleDataException extends RuntimeException {
    public InvalidSaleDataException(String message) {
        super(message);
    }

    public InvalidSaleDataException(String message, Throwable cause) {
        super(message, cause);
    }
}


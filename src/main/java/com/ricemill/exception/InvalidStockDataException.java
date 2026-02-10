package com.ricemill.exception;

public class InvalidStockDataException extends RuntimeException {
    public InvalidStockDataException(String message) {
        super(message);
    }

    public InvalidStockDataException(String message, Throwable cause) {
        super(message, cause);
    }
}


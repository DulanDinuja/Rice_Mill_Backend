package com.ricemill.exception;

public class DuplicateBatchNumberException extends RuntimeException {
    public DuplicateBatchNumberException(String message) {
        super(message);
    }

    public DuplicateBatchNumberException(String message, Throwable cause) {
        super(message, cause);
    }
}


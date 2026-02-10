package com.ricemill.exception;

public class CustomerCreditLimitExceededException extends RuntimeException {
    public CustomerCreditLimitExceededException(String message) {
        super(message);
    }

    public CustomerCreditLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}


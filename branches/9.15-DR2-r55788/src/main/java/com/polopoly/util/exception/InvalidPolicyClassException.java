package com.polopoly.util.exception;

public class InvalidPolicyClassException extends PolicyGetException {
    public InvalidPolicyClassException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPolicyClassException(String message) {
        super(message);
    }
}

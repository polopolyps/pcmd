package com.polopoly.util.exception;

public class ReferenceNotSetException extends PolicyGetException {
    public ReferenceNotSetException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReferenceNotSetException(String message) {
        super(message);
    }
}

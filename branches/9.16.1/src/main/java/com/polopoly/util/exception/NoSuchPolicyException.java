package com.polopoly.util.exception;

public class NoSuchPolicyException extends PolicyGetException {

    public NoSuchPolicyException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchPolicyException(String message) {
        super(message);
    }

}

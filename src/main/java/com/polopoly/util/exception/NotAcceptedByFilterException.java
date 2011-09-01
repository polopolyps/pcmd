package com.polopoly.util.exception;

public class NotAcceptedByFilterException extends NoSuchPolicyException {

    public NotAcceptedByFilterException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAcceptedByFilterException(String message) {
        super(message);
    }

}

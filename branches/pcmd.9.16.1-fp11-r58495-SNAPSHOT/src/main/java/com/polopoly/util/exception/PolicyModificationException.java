package com.polopoly.util.exception;


public class PolicyModificationException extends CMModificationException {

    public PolicyModificationException(String message) {
        super(message);
    }

    public PolicyModificationException(String message, Exception cause) {
        super(message, cause);
    }

}

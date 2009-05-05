package com.polopoly.util.exception;

import com.polopoly.cm.client.CMException;

public class PolicyCreateException extends CMException {
    public PolicyCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public PolicyCreateException(String message) {
        super(message);
    }
}

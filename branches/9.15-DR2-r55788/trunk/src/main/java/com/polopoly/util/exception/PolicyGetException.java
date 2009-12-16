package com.polopoly.util.exception;

import com.polopoly.cm.client.CMException;

public class PolicyGetException extends CMException {
    public PolicyGetException(String message, Throwable cause) {
        super(message, cause);
    }

    public PolicyGetException(String message) {
        super(message);
    }
}

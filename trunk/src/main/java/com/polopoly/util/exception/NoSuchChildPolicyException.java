package com.polopoly.util.exception;

import com.polopoly.cm.client.CMRuntimeException;

public class NoSuchChildPolicyException extends CMRuntimeException {
    public NoSuchChildPolicyException(String message, Throwable e) {
        super(message, e);
    }

    public NoSuchChildPolicyException(String message) {
        super(message);
    }
}

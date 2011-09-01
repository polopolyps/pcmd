package com.polopoly.util.exception;

import com.polopoly.cm.client.CMException;

public class CMModificationException extends CMException {

    public CMModificationException(String message) {
        super(message);
    }

    public CMModificationException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

}

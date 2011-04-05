package com.polopoly.util.exception;

import com.polopoly.cm.client.CMException;

public class ContentGetException extends CMException {
    public ContentGetException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}

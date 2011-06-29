package com.polopoly.ps.pcmd.argument;

public class ArgumentException extends Exception {

    public ArgumentException(String message) {
        super(message);
    }

    public ArgumentException(String message, Throwable t) {
        super(message, t);
    }
}

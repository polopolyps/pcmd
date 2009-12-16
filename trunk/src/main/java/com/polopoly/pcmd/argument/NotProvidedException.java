package com.polopoly.pcmd.argument;

public class NotProvidedException extends ArgumentException {

    public NotProvidedException(String argument) {
        super("Argument " + argument + " not provided.");
    }

}

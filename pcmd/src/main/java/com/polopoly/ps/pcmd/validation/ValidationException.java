package com.polopoly.ps.pcmd.validation;

public class ValidationException extends Exception {
    private String context;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Exception cause) {
        super(message, cause);
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public String getMessage() {
        if (context != null) {
            return "While validating " + context + ":" + super.getMessage();
        }

        return super.getMessage();
    }

}

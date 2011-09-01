package com.polopoly.util.exception;

public class UserNotLoggedInException extends Exception {

    public UserNotLoggedInException(Exception e) {
        super(e);
    }

    public UserNotLoggedInException() {
    }

    public UserNotLoggedInException(String message, Throwable cause) {
        super(message, cause);
    }

}

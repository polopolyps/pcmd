package com.polopoly.util.client;

@SuppressWarnings("serial")
public class LoginRequiredException extends RuntimeException {

    public LoginRequiredException() {
        super("No password was specified, so no caller was logged in. However, an " +
          "operation was attempted that requires login. Please specify a password and retry.");
    }

}

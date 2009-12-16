package com.polopoly.util.client;

import java.io.PrintStream;
import java.io.PrintWriter;

public class LoginRequiredException extends RuntimeException {

    public LoginRequiredException() {
        super("No password was specified, so no caller was logged in. However, an " +
          "operation was attempted that requires login. Please specify a password and retry.");
    }

    @Override
    public void printStackTrace(PrintStream s) {
        // hide ugly stacktraces when using PCMD.
        return;
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        // hide ugly stacktraces when using PCMD.
        return;
    }

}

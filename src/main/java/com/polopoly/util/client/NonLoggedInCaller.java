package com.polopoly.util.client;

import java.io.IOException;
import java.io.ObjectOutputStream;

import com.polopoly.user.server.Caller;
import com.polopoly.user.server.UserId;

public class NonLoggedInCaller extends Caller {
    private String userName;

    NonLoggedInCaller(UserId userId, String sessionKey,
            String secureSessionKey, String userName) {
        super(userId, sessionKey, secureSessionKey);

        this.userName = userName;
    }

    @Override
    public String getLoginName() {
        return userName;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        // if we attempt to do something (e.g. set an ACL) that requires login
        // but this is the current caller, it will be serialized and set to
        // the user server. That will fail, since the class is not available
        // to the application server, and anyway we apparently need to log in,
        // so intercept any attempt to serialize the caller).
        throw new LoginRequiredException();
    }
}
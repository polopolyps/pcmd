package com.polopoly.util.client;

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
}
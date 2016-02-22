package com.polopoly.pcmd.tool;

import org.junit.runner.RunWith;

import com.polopoly.testbase.ImportTestContent;
import com.polopoly.testbase.TestBaseRunner;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.User;
import com.polopoly.user.server.UserId;
import com.polopoly.user.server.UserServer;


@ImportTestContent
@RunWith(TestBaseRunner.class)
public abstract class AbstractIntegrationTestBase {
	
	public static final String DEFAULT_USER = "sysadmin";
	public static final String DEFAULT_PASSWORD = "sysadmin";
	
	public Caller login(UserServer userServer, String username, String password) {
        try {
            Caller caller = userServer.loginAndMerge(username, password, null);
            return caller;
        } catch (Throwable e) {
            throw new RuntimeException("Failed to login user [" + username + "]", e);
        }
    }
    
	public void logout(UserServer userServer, Caller caller) {
        try {
            UserId userId = caller.getUserId();
            if (caller.isLoggedIn(userServer)) {
                User user = userServer.getUserByUserId(userId);
                user.logout(caller);
            } 
        } catch (Throwable e) {
            throw new RuntimeException("Failed to log out user [" + caller.getLoginName() + "]", e);
        }
    }
}

package com.polopoly.pcmd.parser;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.polopoly.user.server.User;
import com.polopoly.user.server.UserId;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.client.PolopolyContext;

public class UserParser implements Parser<User> {
    private UserServer userServer;

    public UserParser() {
    }

    public UserParser(PolopolyContext context) {
        userServer = context.getUserServer();
    }

    public String getHelp() {
        return "User name or principal ID";
    }

    public User parse(String parameter) throws ParseException {
        if (userServer == null) {
            throw new ParseException(this, parameter, "No context provided in constructor.");
        }

        try {
            UserId userId = new UserId(parameter);
            return userServer.getUserByUserId(userId);
        } catch (NumberFormatException e) {
        } catch (IllegalArgumentException e) {
        } catch (RemoteException e) {
            System.err.println(e.toString());
            System.exit(1);
        } catch (CreateException e) {
        }

        try {
            return userServer.getUserByLoginName(parameter);
        } catch (RemoteException e) {
            System.err.println(e.toString());
            System.exit(1);
        } catch (FinderException e) {
            System.err.println(e.toString());
            System.exit(1);
        }

        throw new ParseException(this, parameter, "not a known user name or principal ID.");
    }
}

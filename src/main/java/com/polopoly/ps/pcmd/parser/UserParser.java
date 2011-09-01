package com.polopoly.ps.pcmd.parser;

import java.rmi.RemoteException;

import javax.ejb.FinderException;

import com.polopoly.user.server.User;
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
            throw new ParseException(this, parameter,
                    "No context provided in constructor.");
        }

        try {
            return userServer.getUserByIntegerId(Integer.valueOf(parameter));
        } catch (NumberFormatException e) {
        } catch (IllegalArgumentException e) {
        } catch (RemoteException e) {
            throw new ParseException(this, parameter, e);
        } catch (FinderException e) {
        }

        try {
            return userServer.getUserByLoginName(parameter);
        } catch (RemoteException e) {
            throw new ParseException(this, parameter, e);
        } catch (FinderException e) {
        }

        throw new ParseException(this, parameter,
                "not a known user name or principal ID.");
    }
}

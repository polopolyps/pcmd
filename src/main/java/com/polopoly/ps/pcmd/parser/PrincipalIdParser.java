package com.polopoly.ps.pcmd.parser;

import java.rmi.RemoteException;

import com.polopoly.user.server.PrincipalId;
import com.polopoly.util.client.PolopolyContext;

public class PrincipalIdParser implements Parser<PrincipalId> {
    private UserParser userParser;
    private GroupParser groupParser;

    public PrincipalIdParser(PolopolyContext context) {
        userParser = new UserParser(context);
        groupParser = new GroupParser(context);
    }

    public PrincipalIdParser() {
    }

    public String getHelp() {
        return "User name, group name or principal ID";
    }

    public PrincipalId parse(String parameter) throws ParseException {
        if (userParser == null) {
            throw new ParseException(this, parameter, "No context passed. Cannot parse.");
        }

        try {
            try {
                return userParser.parse(parameter).getUserId();
            }
            catch (ParseException e) {
                return groupParser.parse(parameter).getGroupId();
            }
        } catch (RemoteException e) {
            throw new ParseException(this, parameter, "While getting principal ID: " + e);
        }
    }

}

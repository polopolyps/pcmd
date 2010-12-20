package com.polopoly.pcmd.parser;

import java.rmi.RemoteException;

import javax.ejb.FinderException;

import com.polopoly.user.server.Group;
import com.polopoly.user.server.GroupId;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.client.PolopolyContext;

public class GroupParser implements Parser<Group> {
    private UserServer userServer;

    public GroupParser() {
    }

    public GroupParser(PolopolyContext context) {
        userServer = context.getUserServer();
    }

    public String getHelp() {
        return "Group name or principal ID";
    }

    public Group parse(String parameter) throws ParseException {
        if (userServer == null) {
            throw new ParseException(this, parameter, "No context provided in constructor.");
        }

        try {
            GroupId groupId = new GroupId(parameter);
            return userServer.findGroup(groupId);
        } catch (NumberFormatException e) {
        } catch (IllegalArgumentException e) {
        } catch (RemoteException e) {
            throw new ParseException(this, parameter, e);
        } catch (FinderException e) {
        }

        try {
            GroupId[] groups = userServer.findGroupsByName(parameter);

            if (groups.length > 1) {
                System.err.println("There are multiple groups with name \"" + parameter + "\". Using first.");
            }

            if (groups.length > 0) {
                return userServer.findGroup(groups[0]);
            }
        } catch (RemoteException e) {
            throw new ParseException(this, parameter, e);
        } catch (FinderException e) {
        }

        throw new ParseException(this, parameter, "not a known group name or principal ID.");
    }
}

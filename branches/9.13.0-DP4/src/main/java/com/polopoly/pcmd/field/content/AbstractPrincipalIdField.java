package com.polopoly.pcmd.field.content;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.polopoly.user.server.Group;
import com.polopoly.user.server.GroupId;
import com.polopoly.user.server.PrincipalId;
import com.polopoly.user.server.User;
import com.polopoly.user.server.UserId;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.client.PolopolyContext;

public class AbstractPrincipalIdField {
    public static String get(PrincipalId principalId, PolopolyContext context) {
        UserServer userServer = context.getUserServer();

        if (principalId instanceof UserId) {
            try {
                User user = userServer.getUserByUserId((UserId) principalId);

                return user.getLoginName();
            } catch (RemoteException e) {
                System.err.println(principalId.getPrincipalIdString() + ": " + e.toString());

                return principalId.getPrincipalIdString();
            } catch (IllegalArgumentException e) {
                System.err.println(principalId.getPrincipalIdString() + ": " + e.toString());

                return principalId.getPrincipalIdString();
            } catch (CreateException e) {
                System.err.println(principalId.getPrincipalIdString() + ": " + e.toString());

                return principalId.getPrincipalIdString();
            }
        }
        else if (principalId instanceof GroupId) {
            try {
                Group group = userServer.findGroup((GroupId) principalId);

                return "group:" + group.getName();
            } catch (RemoteException e) {
                System.err.println(principalId.getPrincipalIdString() + ": " + e.toString());

                return principalId.getPrincipalIdString();
            } catch (FinderException e) {
                System.err.println(principalId.getPrincipalIdString() + ": " + e.toString());

                return principalId.getPrincipalIdString();
            }
        }
        else {
            return principalId.getPrincipalIdString();
        }
    }
}

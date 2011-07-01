package com.polopoly.ps.pcmd.tool;

import static com.polopoly.ps.pcmd.parser.ContentFieldListParser.PREFIX_FIELD_SEPARATOR;

import java.rmi.RemoteException;

import javax.ejb.CreateException;

import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.parser.ContentFieldListParser;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.InvalidSessionKeyException;
import com.polopoly.user.server.PermissionDeniedException;
import com.polopoly.user.server.User;
import com.polopoly.user.server.UserId;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.client.PolopolyContext;

public class UserTool implements Tool<UserParameters> {
    public UserParameters createParameters() {
        return new UserParameters();
    }

    public void execute(PolopolyContext context, UserParameters parameters) {
        if (parameters.getUsers().isEmpty()) {
            printAllUsers(context);
        }
        else {
            for (User user : parameters.getUsers()) {
                printUser(user, context);
            }
        }
    }

    private void printAllUsers(PolopolyContext context) {
        UserServer userServer = context.getUserServer();

        String loginName = "%";

        try {
            UserId[] ids = userServer.findUserIdsByAttributeValue(
                    "user", "loginName", loginName,
                    context.getPolicyCMServer().getCurrentCaller());

            for (UserId userId : ids) {
                printUser(userId, context);
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    private void printUser(UserId userId, PolopolyContext context) {
        try {
            User user = context.getUserServer().getUserByUserId(userId);

            printUser(user, context);
        } catch (RemoteException e) {
            System.err.println(e.toString());
        } catch (CreateException e) {
            System.err.println(e.toString());
        }
    }

    private void printUser(User user, PolopolyContext context) {
        try {
            System.out.println(user.getLoginName());

            if (user.isLdapUser()) {
                System.out.println("(is LDAP user)");
            }

            Caller caller = context.getPolicyCMServer().getCurrentCaller();

            String[] groupNames = user.getPersistentGroupNames(caller);

            for (String groupName : groupNames) {
                String[] attributeNames = user.getPersistentAttributeNames(groupName, caller);

                for (String attributeName : attributeNames) {
                    try {
                        String value = user.getPersistent(groupName, attributeName, caller);

                        System.out.println(
                                ContentFieldListParser.COMPONENT + PREFIX_FIELD_SEPARATOR +
                                groupName + ':' + attributeName + '=' + value);
                    } catch (PermissionDeniedException e) {
                        System.err.println(e.toString());
                    } catch (InvalidSessionKeyException e) {
                        System.err.println(e.toString());
                    }
                }
            }

            System.out.println(ContentFieldListParser.ID + PREFIX_FIELD_SEPARATOR +
                user.getUserId().getPrincipalIdString());
        } catch (RemoteException e) {
            System.err.println(e.toString());
        }
    }

    public String getHelp() {
        return "Prints information on the users in the system.";
    }

}

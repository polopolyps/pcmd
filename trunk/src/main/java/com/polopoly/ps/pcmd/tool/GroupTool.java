package com.polopoly.ps.pcmd.tool;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.FinderException;

import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.Arguments;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.ps.pcmd.argument.Parameters;
import com.polopoly.ps.pcmd.field.content.AbstractPrincipalIdField;
import com.polopoly.user.server.Group;
import com.polopoly.user.server.GroupId;
import com.polopoly.user.server.PrincipalId;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.client.PolopolyContext;

public class GroupTool implements Tool<GroupParameters> {
    public GroupParameters createParameters() {
        return new GroupParameters();
    }

    @SuppressWarnings("unchecked")
    public void execute(PolopolyContext context, GroupParameters parameters) {
        UserServer userServer = context.getUserServer();

        try {
            if (parameters.getGroupCount() == 0) {
                try {
                    GroupId[] groups = userServer.getAllGroups();

                    for (int i = 0; i < groups.length; i++) {
                        GroupId groupId = groups[i];

                        try {
                            Group group = userServer.findGroup(groupId);

                            System.out.println(group.getName());
                        } catch (FinderException e) {
                            throw new FatalToolException("While fetching group: " + e.getMessage(), e);
                        }
                    }
                } catch (RemoteException e) {
                    throw new FatalToolException(e);
                }
            }
            else {
                for (Group group : parameters) {
                    System.out.println("group:" + group.getName());
                    System.out.println("groupId:" + group.getGroupId().getGroupIdInt());

                    Iterator<PrincipalId> it = group.owners();

                    while (it.hasNext()) {
                        PrincipalId principalId = it.next();

                        System.out.println("owner:" + AbstractPrincipalIdField.get(principalId, context));
                    }

                    it = group.members();

                    while (it.hasNext()) {
                        PrincipalId principalId = it.next();

                        System.out.println(AbstractPrincipalIdField.get(principalId, context));
                    }
                }
            }
        } catch (RemoteException e) {
            throw new FatalToolException(e);
        }
    }

    public String getHelp() {
        return "Prints membership of one or several groups.";
    }
}

class GroupParameters implements Parameters, Iterable<Group> {
    private List<Group> groups = new ArrayList<Group>();

    public void getHelp(ParameterHelp help) {
        help.setArguments(null, "The names of the groups to list membership of.");
    }

    public void parseParameters(Arguments args, PolopolyContext context)
            throws ArgumentException {
        UserServer userServer = context.getUserServer();

        for (int i = 0; i < args.getArgumentCount(); i++) {
            String groupName = args.getArgument(i);

            try {
                GroupId groupId = new GroupId(Integer.parseInt(groupName));
                addGroup(userServer.findGroup(groupId));
                continue;
            } catch (NumberFormatException e) {
            } catch (IllegalArgumentException e) {
            } catch (RemoteException e) {
                throw new FatalToolException(e);
            } catch (FinderException e) {
            }

            try {
                GroupId[] groups = userServer.findGroupsByName(groupName);

                if (groups.length == 0) {
                    throw new FatalToolException("No groups found with name or ID \"" + groupName + "\".");
                }

                for (int j = 0; j < groups.length; j++) {
                    addGroup(userServer.findGroup(groups[j]));
                }
            } catch (RemoteException e) {
                throw new FatalToolException(e);
            } catch (FinderException e) {
                throw new FatalToolException(e);
            }
        }
    }

    public void addGroup(Group group) {
        groups.add(group);
    }

    public int getGroupCount() {
        return groups.size();
    }

    public Iterator<Group> iterator() {
        return groups.iterator();
    }
}
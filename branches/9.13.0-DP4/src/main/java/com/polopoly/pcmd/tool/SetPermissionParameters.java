package com.polopoly.pcmd.tool;

import com.polopoly.pcmd.argument.ArgumentException;
import com.polopoly.pcmd.argument.Arguments;
import com.polopoly.pcmd.argument.ContentIdListParameters;
import com.polopoly.pcmd.argument.NotProvidedException;
import com.polopoly.pcmd.argument.ParameterHelp;
import com.polopoly.pcmd.parser.ContentIdParser;
import com.polopoly.user.server.Group;
import com.polopoly.user.server.GroupId;
import com.polopoly.user.server.PrincipalId;
import com.polopoly.util.client.PolopolyContext;

public class SetPermissionParameters extends ContentIdListParameters {
    private String user;
    private String group;
    private String permission;

    private static final String USER_PARAMETER = "user";
    private static final String GROUP_PARAMETER = "group";
    private static final String PERMISSION_PARAMETER = "permission";

    public PrincipalId getPrincipalId(PolopolyContext context) throws ArgumentException {
        if (group != null) {
            try {
                try {
                    int groupInt = Integer.parseInt(group);

                    // get the group to verify it exists.
                    Group groupObject = context.getUserServer().findGroup(new GroupId(groupInt));

                    return groupObject.getGroupId();
                } catch (NumberFormatException e) {
                    GroupId[] groupObject;
                    groupObject = context.getUserServer().findGroupsByName(group);

                    if (groupObject.length == 0) {
                        throw new ArgumentException("Found no group with name \"" + group + "\".");
                    }

                    return groupObject[0];
                }
            } catch (Exception e) {
                throw new ArgumentException("While fetching group \"" + group + "\": " + e.getMessage());
            }
        } else {
            String userName = user;
            if (userName != null) {
                return ContentIdParser.getUser(context, userName);
            }
            else {
                throw new ArgumentException("Neither " + USER_PARAMETER + " nor " + GROUP_PARAMETER + " specified.");
            }
        }
    }

    @Override
    public void getHelp(ParameterHelp help) {
        super.getHelp(help);

        help.addOption(USER_PARAMETER, null, "The name of the user for which to add a permission.");
        help.addOption(GROUP_PARAMETER, null, "The name of the group for which to add a permission.");
        help.addOption(PERMISSION_PARAMETER, null, "The name of the permission to add.");
    }

    @Override
    public void parseParameters(Arguments args, PolopolyContext context)
            throws ArgumentException {
        super.parseParameters(args, context);

        user = args.getOptionString(USER_PARAMETER, null);
        group = args.getOptionString(GROUP_PARAMETER, null);

        if (user == null && group == null) {
            throw new NotProvidedException("Either the " + USER_PARAMETER +
                    " or the " + GROUP_PARAMETER + " parameter must be specified.");
        }

        permission = args.getOptionString(PERMISSION_PARAMETER);
    }

    public void setUser(String user) {
        this.user = user;
    }


    public String getUser() {
        return user;
    }


    public void setGroup(String group) {
        this.group = group;
    }


    public String getGroup() {
        return group;
    }


    public void setPermission(String permission) {
        this.permission = permission;
    }


    public String getPermission() {
        return permission;
    }

}

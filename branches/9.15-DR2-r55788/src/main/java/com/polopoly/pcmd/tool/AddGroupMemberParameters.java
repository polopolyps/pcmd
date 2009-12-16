package com.polopoly.pcmd.tool;

import java.util.ArrayList;
import java.util.List;

import com.polopoly.pcmd.argument.ArgumentException;
import com.polopoly.pcmd.argument.Arguments;
import com.polopoly.pcmd.argument.ParameterHelp;
import com.polopoly.pcmd.argument.Parameters;
import com.polopoly.pcmd.parser.GroupParser;
import com.polopoly.pcmd.parser.PrincipalIdParser;
import com.polopoly.user.server.Group;
import com.polopoly.user.server.PrincipalId;
import com.polopoly.util.client.PolopolyContext;

public class AddGroupMemberParameters implements Parameters {
    private static final String GROUP_OPTION = "group";
    private Group group;
    private List<PrincipalId> members;

    public void getHelp(ParameterHelp help) {
        help.setArguments(new PrincipalIdParser(),
                "The user(s) or group(s) to add.");
        help.addOption(GROUP_OPTION, new GroupParser(),
            "The group to add members to. May be specified multiple times.");
    }

    public void parseParameters(Arguments args, PolopolyContext context)
            throws ArgumentException {
        group = args.getOption(GROUP_OPTION, new GroupParser(context));
        members = new ArrayList<PrincipalId>();

        for (int i = 0; i < args.getArgumentCount(); i++) {
            members.add(args.getArgument(i, new PrincipalIdParser(context)));
        }
    }

    public Group getGroup() {
        return group;
    }

    public List<PrincipalId> getMembers() {
        return members;
    }
}



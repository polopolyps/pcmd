package com.polopoly.pcmd.tool;

import java.util.ArrayList;
import java.util.List;

import com.polopoly.pcmd.argument.ArgumentException;
import com.polopoly.pcmd.argument.Arguments;
import com.polopoly.pcmd.argument.NotProvidedException;
import com.polopoly.pcmd.argument.ParameterHelp;
import com.polopoly.pcmd.argument.Parameters;
import com.polopoly.pcmd.parser.PrincipalIdParser;
import com.polopoly.pcmd.parser.UserParser;
import com.polopoly.user.server.PrincipalId;
import com.polopoly.util.client.PolopolyContext;

public class CreateGroupParameters implements Parameters {
    private static final String OWNER_OPTION = "owner";
    private static final String MEMBER_OPTION = "member";

    private List<String> groupNamesToCreate = new ArrayList<String>();
    private List<PrincipalId> members;
    private List<PrincipalId> owners;

    public void getHelp(ParameterHelp help) {
        help.setArguments(null, "The name of the group to create. Multiple arguments may be specified to create several groups.");
        help.addOption(OWNER_OPTION, new UserParser(), "The owner of the new group. Defaults to sysadmin.");
        help.addOption(MEMBER_OPTION, new UserParser(), "A member of the new group. Multiple members may be specified.");
    }

    public void parseParameters(Arguments args, PolopolyContext context)
            throws ArgumentException {
        for (int argumentIndex = 0; argumentIndex < args.getArgumentCount(); argumentIndex++) {
            groupNamesToCreate.add(args.getArgument(argumentIndex));
        }

        try {
            members = args.getOptions(MEMBER_OPTION, new PrincipalIdParser(context));
        }
        catch (NotProvidedException npe) {
            members = new ArrayList<PrincipalId>();
        }

        try {
            owners = args.getOptions(OWNER_OPTION, new PrincipalIdParser(context));
        }
        catch (NotProvidedException npe) {
            owners = new ArrayList<PrincipalId>();
        }
    }

    public List<PrincipalId> getOwners() {
        return owners;
    }

    public List<PrincipalId> getMembers() {
        return members;
    }

    public List<String> getGroupNamesToCreate() {
        return groupNamesToCreate;
    }
}

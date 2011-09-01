package com.polopoly.ps.pcmd.tool;

import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.Arguments;
import com.polopoly.ps.pcmd.argument.ContentIdListParameters;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.util.client.PolopolyContext;

public class CheckPermissionParameters extends ContentIdListParameters {
    private String permission;

    private static final String PERMISSION_PARAMETER = "permission";

    @Override
    public void getHelp(ParameterHelp help) {
        super.getHelp(help);

        help.addOption(PERMISSION_PARAMETER, null, "The permission to check.");
    }

    @Override
    public void parseParameters(Arguments args, PolopolyContext context)
            throws ArgumentException {
        super.parseParameters(args, context);

        setPermission(args.getOptionString(PERMISSION_PARAMETER));
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}

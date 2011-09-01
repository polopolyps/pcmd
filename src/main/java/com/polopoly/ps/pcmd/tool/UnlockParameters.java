package com.polopoly.ps.pcmd.tool;

import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.Arguments;
import com.polopoly.ps.pcmd.argument.ContentIdListParameters;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.ps.pcmd.parser.BooleanParser;
import com.polopoly.util.client.PolopolyContext;

public class UnlockParameters extends ContentIdListParameters {
    private static final String ALL_PARAMETER = "all";

    private boolean unlockAll;

    @Override
    public void getHelp(ParameterHelp help) {
        super.getHelp(help);

        help.addOption(ALL_PARAMETER, new BooleanParser(), "Whether to unlock all locked objects.");
    }

    @Override
    public void parseParameters(Arguments args, PolopolyContext context)
            throws ArgumentException {
        setUnlockAll(args.getFlag(ALL_PARAMETER, false));

        super.parseParameters(args, context);
    }

    @Override
    public boolean isIdsFromStandardInIfNotArgument() {
        return !isUnlockAll();
    }

    public boolean isUnlockAll() {
        return unlockAll;
    }

    public void setUnlockAll(boolean unlockAll) {
        this.unlockAll = unlockAll;
    }

}

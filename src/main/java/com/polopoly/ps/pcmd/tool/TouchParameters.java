package com.polopoly.ps.pcmd.tool;

import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.Arguments;
import com.polopoly.ps.pcmd.argument.ContentIdListParameters;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.ps.pcmd.argument.Parameters;
import com.polopoly.ps.pcmd.parser.BooleanParser;
import com.polopoly.util.client.PolopolyContext;

public class TouchParameters extends ContentIdListParameters implements Parameters {
    private static String DRY_RUN_OPTION = "dry-run";
    private static String QUIET_OPTION = "quiet";

    // I always really wanted a --dry-run for the unix touch.
    public Boolean dryRun;
    public Boolean quiet;

    public void getHelp(ParameterHelp help) {
        super.getHelp(help);
        help.addOption(DRY_RUN_OPTION, new BooleanParser(), "Don't create new versions, just fetch policies.");
        help.addOption(QUIET_OPTION, new BooleanParser(), "Don't print output.");
    }

    public void parseParameters(Arguments args, PolopolyContext context) throws ArgumentException {
        super.parseParameters(args, context);
        dryRun = args.getOption(DRY_RUN_OPTION, new BooleanParser(), "false");
        quiet = args.getOption(QUIET_OPTION, new BooleanParser(), "false");
    }

    public Boolean isDryRun() {
        return dryRun;
    }

    public Boolean isQuiet() {
        return quiet;
    }

}

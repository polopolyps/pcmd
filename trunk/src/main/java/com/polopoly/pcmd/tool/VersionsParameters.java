package com.polopoly.pcmd.tool;

import com.polopoly.pcmd.argument.ArgumentException;
import com.polopoly.pcmd.argument.Arguments;
import com.polopoly.pcmd.argument.ContentIdListParameters;
import com.polopoly.pcmd.argument.ParameterHelp;
import com.polopoly.pcmd.parser.BooleanParser;
import com.polopoly.util.client.PolopolyContext;

public class VersionsParameters extends ContentIdListParameters {
    private static final String PRINT_SYMBOLIC_VERSIONS = "symbolic";

    private boolean printSymbolicVersions;


    public void setPrintSymbolicVersions(boolean printSymbolicVersions) {
        this.printSymbolicVersions = printSymbolicVersions;
    }

    public boolean isPrintSymbolicVersions() {
        return printSymbolicVersions;
    }

    @Override
    public void getHelp(ParameterHelp help) {
        super.getHelp(help);
        help.addOption(PRINT_SYMBOLIC_VERSIONS, new BooleanParser(), "Whether to print which version is LATEST_COMMITTED, LATEST and DEFAULT_STAGE");
    }

    @Override
    public void parseParameters(Arguments args, PolopolyContext context)
            throws ArgumentException {
        super.parseParameters(args, context);

        printSymbolicVersions = args.getFlag(PRINT_SYMBOLIC_VERSIONS, true);
    }
}

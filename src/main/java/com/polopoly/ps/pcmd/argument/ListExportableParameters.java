package com.polopoly.ps.pcmd.argument;

import com.polopoly.ps.pcmd.parser.IntegerParser;
import com.polopoly.util.client.PolopolyContext;

public class ListExportableParameters extends ProjectContentParameters {
    private static final String SINCE_OPTION = "since";
    private int since;

    @Override
    public void getHelp(ParameterHelp help) {
        super.getHelp(help);

        /*
         * help.addOption(SINCE_OPTION, new IntegerParser(),
         * "Return all content created since this version (optional).");
         */
    }

    public int getSince() {
        return since;
    }

    @Override
    public void parseParameters(Arguments args, PolopolyContext context) throws ArgumentException {
        super.parseParameters(args, context);

        try {
            since = args.getOption(SINCE_OPTION, new IntegerParser());
        } catch (NotProvidedException e) {
        }
    }
}

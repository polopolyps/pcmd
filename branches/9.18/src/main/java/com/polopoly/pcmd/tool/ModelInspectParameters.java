package com.polopoly.pcmd.tool;

import com.polopoly.pcmd.argument.ArgumentException;
import com.polopoly.pcmd.argument.Arguments;
import com.polopoly.pcmd.argument.ContentIdListParameters;
import com.polopoly.pcmd.argument.ParameterHelp;
import com.polopoly.pcmd.parser.IntegerParser;
import com.polopoly.util.client.PolopolyContext;

public class ModelInspectParameters extends ContentIdListParameters {
    private static final String DEPTH_PARAMETER = "depth";

    private int depth = 4;
    private static final int MAX_DEPTH = 15;

    @Override
    public void getHelp(ParameterHelp help) {
        super.getHelp(help);

        help.addOption(DEPTH_PARAMETER, new IntegerParser(), "Number of levels to inpsect");
    }

    @Override
    public void parseParameters(Arguments args, PolopolyContext context) throws ArgumentException {

        try {
            setDepth(Integer.parseInt(args.getOptionString(DEPTH_PARAMETER, "3")));
        } catch (NumberFormatException e) {
            System.out.println("Given depth parameter is not a number, default depth will be set to 4. Max allowed depth is " + MAX_DEPTH );
        }
        super.parseParameters(args, context);
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        if ( depth <= MAX_DEPTH) {
            this.depth = depth;
        } else {
            System.out.println("Max depth is " + MAX_DEPTH + " will use that value as depth instead.");
            this.depth = MAX_DEPTH;
        }
    }
}
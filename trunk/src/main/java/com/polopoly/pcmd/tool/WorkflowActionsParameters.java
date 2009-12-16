package com.polopoly.pcmd.tool;

import com.polopoly.pcmd.argument.ArgumentException;
import com.polopoly.pcmd.argument.Arguments;
import com.polopoly.pcmd.argument.ContentIdListParameters;
import com.polopoly.pcmd.argument.ParameterHelp;
import com.polopoly.util.client.PolopolyContext;

public class WorkflowActionsParameters extends ContentIdListParameters {
    private static final String PERFORM_OPTION = "perform";

    private String perform;

    @Override
    public void getHelp(ParameterHelp help) {
        help.addOption(PERFORM_OPTION, null, "The action to perform (optional; if not included available actions are listed)");
    }

    @Override
    public void parseParameters(Arguments args, PolopolyContext context)
            throws ArgumentException {
        super.parseParameters(args, context);
        setPerform(args.getOptionString(PERFORM_OPTION, null));
    }

    public void setPerform(String perform) {
        this.perform = perform;
    }

    public String getPerform() {
        return perform;
    }
}

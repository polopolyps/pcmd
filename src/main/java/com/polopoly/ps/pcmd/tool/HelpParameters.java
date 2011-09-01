package com.polopoly.ps.pcmd.tool;

import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.Arguments;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.ps.pcmd.argument.Parameters;
import com.polopoly.util.client.PolopolyContext;

public class HelpParameters implements Parameters {
    private String tool;

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public void getHelp(ParameterHelp help) {
        help.setArguments(null, "The tool name to return help for.");
    }

    public void parseParameters(Arguments args, PolopolyContext context)
            throws ArgumentException {
        if (args.getArgumentCount() > 0) {
            setTool(args.getArgument(0));
        }
    }
}

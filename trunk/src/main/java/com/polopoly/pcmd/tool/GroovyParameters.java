package com.polopoly.pcmd.tool;

import com.polopoly.pcmd.argument.ArgumentException;
import com.polopoly.pcmd.argument.Arguments;
import com.polopoly.pcmd.argument.ParameterHelp;

public class GroovyParameters extends ListParameters {
    private String groovy = "";
    private boolean modify;

    @Override
    public void parseParameters(Arguments args, PolopolyContext context) throws ArgumentException {
        super.parseParameters(args, context);

        setGroovy(args.getArgument(0));
        setModify(args.getFlag("modify", false));
    }

    @Override
    public void getHelp(ParameterHelp help) {
        help.setArguments(null, "The Groovy code to execute.");

        super.getHelp(help);

        help.addOption("modify", null, "Flag indicating whether to, for each object, first create a new version, then execute the Groovy code, then commit the new version. Defaults to false.");
    }

    @Override
    protected int getFirstContentIdIndex() {
        return 1;
    }

    public String getGroovy() {
        return groovy;
    }

    public void setGroovy(String groovy) {
        this.groovy = groovy;
    }

    public void setModify(boolean modify) {
        this.modify = modify;
    }


    public boolean isModify() {
        return modify;
    }
}

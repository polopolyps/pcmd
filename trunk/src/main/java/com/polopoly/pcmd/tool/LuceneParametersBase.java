package com.polopoly.pcmd.tool;

import com.polopoly.pcmd.argument.ArgumentException;
import com.polopoly.pcmd.argument.Arguments;
import com.polopoly.pcmd.argument.ParameterHelp;
import com.polopoly.pcmd.argument.Parameters;
import com.polopoly.util.client.PolopolyContext;

public class LuceneParametersBase implements Parameters {
    static final String DEFAULT_INDEX = "DefaultIndex";

    static final String INDEX = "index";

    private String index = DEFAULT_INDEX;

    public void getHelp(ParameterHelp help) {
        help.addOption(INDEX, null, "The index to search in. Defaults to " + DEFAULT_INDEX + ".");
    }

    public void parseParameters(Arguments args, PolopolyContext context)
            throws ArgumentException {
        setIndex(args.getOptionString(INDEX, index));
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getIndex() {
        return index;
    }
}

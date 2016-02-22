package com.polopoly.ps.pcmd.tool;

import static com.polopoly.ps.pcmd.tool.LuceneParametersBase.DEFAULT_INDEX;
import static com.polopoly.ps.pcmd.tool.LuceneParametersBase.INDEX;

import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.Arguments;
import com.polopoly.ps.pcmd.argument.ContentIdListParameters;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.util.client.PolopolyContext;

public class LuceneInspectParameters extends ContentIdListParameters {
    private String index = DEFAULT_INDEX;

    @Override
    public void getHelp(ParameterHelp help) {
        super.getHelp(help);
        help.addOption(INDEX, null, "The index to search in. Defaults to " + DEFAULT_INDEX + ".");
    }

    @Override
    public void parseParameters(Arguments args, PolopolyContext context)
            throws ArgumentException {
        super.parseParameters(args, context);
        setIndex(args.getOptionString(INDEX, index));
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getIndex() {
        return index;
    }
}

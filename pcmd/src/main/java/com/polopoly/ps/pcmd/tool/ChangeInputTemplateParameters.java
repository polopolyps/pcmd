package com.polopoly.ps.pcmd.tool;

import com.polopoly.cm.ContentId;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.Arguments;
import com.polopoly.ps.pcmd.argument.ContentIdListParameters;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.ps.pcmd.parser.ContentIdParser;
import com.polopoly.util.client.PolopolyContext;

/**
 * @author gmola
 *         date: 11/27/12
 */
public class ChangeInputTemplateParameters extends ContentIdListParameters {

    private ContentId inputTemplate;

    @Override
    public void parseParameters(Arguments args, PolopolyContext context) throws ArgumentException {
        super.parseParameters(args, context);

        if (args.getArgument(0).isEmpty()) {
            throw new ArgumentException("invalid input template");
        }

        inputTemplate = new ContentIdParser(context).parse(args.getArgument(0));

    }

    public ContentId getInputTemplate() {
        return inputTemplate;
    }

    @Override
    public void getHelp(ParameterHelp help) {
        help.setArguments(new ContentIdParser(), "new input template");
        super.getHelp(help);
    }

}

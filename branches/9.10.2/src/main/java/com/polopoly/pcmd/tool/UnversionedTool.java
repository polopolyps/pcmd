package com.polopoly.pcmd.tool;

import java.util.Iterator;

import com.polopoly.cm.ContentId;
import com.polopoly.pcmd.argument.ArgumentException;
import com.polopoly.pcmd.argument.Arguments;
import com.polopoly.pcmd.argument.ContentIdListParameters;
import com.polopoly.pcmd.argument.NotProvidedException;
import com.polopoly.util.client.PolopolyContext;

public class UnversionedTool implements Tool<ContentIdListParameters> {

    public ContentIdListParameters createParameters() {
        return new ContentIdListParameters();
    }

    public void execute(PolopolyContext context,
            ContentIdListParameters parameters) {
        Iterator<ContentId> it = parameters.getContentIds();

        while (it.hasNext()) {
            System.out.println(it.next().getContentId().getContentIdString());
        }
    }

    public void parseParameters(Arguments args,
            ContentIdListParameters parameters, PolopolyContext context)
            throws ArgumentException {
        try {
            parameters.setContentIds(args.getArgumentContentIds(0, parameters.isStopOnException()));
        }
        catch (NotProvidedException npe) {
            parameters.setContentIds(args.getStdInContentIds());
        }
    }

    public String getHelp() {
        return "Turns the specified content IDs into unversioned content IDs.";
    }
}

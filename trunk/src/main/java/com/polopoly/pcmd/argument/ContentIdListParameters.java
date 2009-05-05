package com.polopoly.pcmd.argument;

import java.util.Iterator;

import com.polopoly.cm.ContentId;
import com.polopoly.pcmd.parser.BooleanParser;
import com.polopoly.pcmd.parser.ContentIdParser;
import com.polopoly.util.client.PolopolyContext;

public class ContentIdListParameters implements Parameters {
    static final String STOPONEXCEPTION = "stoponexception";

    private Iterator<ContentId> contentIds;
    private boolean stopOnException = false;

    public void setContentIds(Iterator<ContentId> contentIds) {
        this.contentIds = contentIds;
    }

    public Iterator<ContentId> getContentIds() {
        if (contentIds == null) {
            throw new RuntimeException("parseParameters was never called.");
        }

        return contentIds;
    }

    public void setStopOnException(boolean stopOnException) {
        this.stopOnException = stopOnException;
    }

    public boolean isStopOnException() {
        return stopOnException;
    }

    public void parseParameters(Arguments args, PolopolyContext context)
            throws ArgumentException {
        try {
            setContentIds(args.getArgumentContentIds(getFirstContentIdIndex()));
        }
        catch (NotProvidedException npe) {
            setContentIds(args.getStdInContentIds());
        }

        setStopOnException(args.getFlag(STOPONEXCEPTION, true));
    }

    /**
     * Which is the first argument that is a content ID?
     * Return e.g. 2 if the first two arguments are something else.
     */
    protected int getFirstContentIdIndex() {
        return 0;
    }

    public void getHelp(ParameterHelp help) {
        help.addOption(STOPONEXCEPTION, new BooleanParser(),
                "Whether to interrupt the operation when an exception occurs or just ignore it and continue.");
        help.setArguments(new ContentIdParser(), "A series of content IDs.");
    }
}
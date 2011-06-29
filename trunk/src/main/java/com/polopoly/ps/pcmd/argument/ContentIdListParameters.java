package com.polopoly.ps.pcmd.argument;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.polopoly.cm.ContentId;
import com.polopoly.ps.pcmd.parser.BooleanParser;
import com.polopoly.ps.pcmd.parser.ContentIdParser;
import com.polopoly.util.client.PolopolyContext;

public class ContentIdListParameters implements Parameters, Iterable<ContentId> {
    static final String STOPONEXCEPTION = "stoponexception";

    private Iterable<ContentId> contentIds;
    private boolean stopOnException = false;
    private boolean idsFromStandardInIfNotArgument = true;

    public boolean isIdsFromStandardInIfNotArgument() {
        return idsFromStandardInIfNotArgument;
    }

    public void setIdsFromStandardInIfNotArgument(
            boolean idsFromStandardInIfNotArgument) {
        this.idsFromStandardInIfNotArgument = idsFromStandardInIfNotArgument;
    }

    public void setContentIds(Iterable<ContentId> contentIds) {
        this.contentIds = contentIds;
    }

    public Iterator<ContentId> getContentIds() {
        if (contentIds == null) {
            throw new RuntimeException("parseParameters was never called.");
        }

        return contentIds.iterator();
    }

    public void setStopOnException(boolean stopOnException) {
        this.stopOnException = stopOnException;
    }

    public boolean isStopOnException() {
        return stopOnException;
    }


    public void parseParameters(final Arguments args, PolopolyContext context) throws ArgumentException {
        setStopOnException(args.getFlag(STOPONEXCEPTION, true));

        try {
            setContentIds(args.getArgumentContentIds(getFirstContentIdIndex(), isStopOnException()));
        }
        catch (NotProvidedException npe) {
            Iterable<ContentId> contentIdIterable = new Iterable<ContentId>() {
                List<ContentId> emptyList = Collections.emptyList();

                Iterable<ContentId> stdInIterable =
                    new RestartableIterator<ContentId>(args.getStdInContentIds());

                public Iterator<ContentId> iterator() {
                    if (isIdsFromStandardInIfNotArgument()) {
                        return stdInIterable.iterator();
                    }
                    else {
                        return emptyList.iterator();
                    }
                }
            };

            setContentIds(contentIdIterable);
        }
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

    public Iterator<ContentId> iterator() {
        return getContentIds();
    }
}
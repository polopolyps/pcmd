package com.polopoly.ps.pcmd.tool;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.polopoly.cm.ContentId;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.Arguments;
import com.polopoly.ps.pcmd.argument.ContentIdListParameters;
import com.polopoly.ps.pcmd.argument.NotProvidedException;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.ps.pcmd.argument.Parameters;
import com.polopoly.ps.pcmd.argument.RestartableIterator;
import com.polopoly.ps.pcmd.parser.BooleanParser;
import com.polopoly.ps.pcmd.parser.ContentIdParser;
import com.polopoly.util.client.PolopolyContext;

public class SolrInspectParameters extends ContentIdListParameters implements Parameters, Iterable<ContentId> {
    static final String STOPONEXCEPTION = "stoponexception";
    private static final String INDEX_OPTION = "searchindex";
    private static final String RESOLVE_EXTERNAL_ID_OPTION = "resolveid";
    private static final String VERBOSE_OPTION = "verbose";

    private String indexName;
    private boolean resolveIds;
    private boolean verbose;

    @Override
    public void parseParameters(final Arguments args, PolopolyContext context) throws ArgumentException {
        setStopOnException(args.getFlag(STOPONEXCEPTION, true));
        indexName = args.getOptionString(INDEX_OPTION, "public");
        resolveIds = args.getFlag(RESOLVE_EXTERNAL_ID_OPTION, true);
        verbose = args.getFlag(VERBOSE_OPTION, false);

        try {
            setContentIds(args.getArgumentContentIds(getFirstContentIdIndex(), isStopOnException()));
        } catch (NotProvidedException npe) {
            Iterable<ContentId> contentIdIterable = new Iterable<ContentId>() {
                List<ContentId> emptyList = Collections.emptyList();

                Iterable<ContentId> stdInIterable = new RestartableIterator<ContentId>(args.getStdInContentIds());

                public Iterator<ContentId> iterator() {
                    if (isIdsFromStandardInIfNotArgument()) {
                        return stdInIterable.iterator();
                    } else {
                        return emptyList.iterator();
                    }
                }
            };

            setContentIds(contentIdIterable);
        }
    }

    @Override
    public void getHelp(ParameterHelp help) {
        help.setArguments(new ContentIdParser(), "A series of content IDs.");
        help.addOption(STOPONEXCEPTION, new BooleanParser(),
                       "Whether to interrupt the operation when an exception occurs or just ignore it and continue.");
        help.addOption(INDEX_OPTION, null, "The index to search, defaults to public");
        help.addOption(RESOLVE_EXTERNAL_ID_OPTION, new BooleanParser(),
                       "Whether to print external IDs rather than numerical IDs if available (reduces performance; defaults to true).");
        help.addOption(VERBOSE_OPTION, new BooleanParser(),
                       "Print verbose execution log to stderr; defaults to false).");
    }

    public String getIndexName() {
        return indexName;
    }

    public boolean getResolveIds() {
        return resolveIds;
    }

    public boolean verbose() {
        return verbose;
    }

}

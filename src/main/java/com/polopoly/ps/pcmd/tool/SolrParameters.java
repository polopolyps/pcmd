package com.polopoly.ps.pcmd.tool;

import org.apache.solr.client.solrj.SolrQuery;

import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.Arguments;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.ps.pcmd.argument.Parameters;
import com.polopoly.ps.pcmd.parser.BooleanParser;
import com.polopoly.ps.pcmd.parser.IntegerParser;
import com.polopoly.ps.pcmd.parser.SolrQueryParser;
import com.polopoly.util.client.PolopolyContext;

public class SolrParameters implements Parameters {
    private static final String INDEX_OPTION = "index";
    private static final String MAX_HITS_OPTION = "maxhits";
    private static final String RESOLVE_EXTERNAL_ID_OPTION = "resolveid";
    private static final String VERBOSE_OPTION = "verbose";

    private SolrQuery query;
    private String indexName;
    private Integer maxHits;
    private boolean resolveIds;
    private boolean verbose;

    @Override
    public void parseParameters(Arguments args, PolopolyContext context) throws ArgumentException {
        query = args.getArgument(0, new SolrQueryParser());
        indexName = args.getOptionString(INDEX_OPTION, "public");
        maxHits = args.getOption(MAX_HITS_OPTION, new IntegerParser(), "" + Integer.MAX_VALUE);
        resolveIds = args.getFlag(RESOLVE_EXTERNAL_ID_OPTION, true);
        verbose = args.getFlag(VERBOSE_OPTION, false);
    }

    @Override
    public void getHelp(ParameterHelp help) {
        help.setArguments(null, "The solr query");
        help.addOption(INDEX_OPTION, null, "The index to search, defaults to public");
        help.addOption(MAX_HITS_OPTION, null, "The maximum number of hits returned, defaults to " + maxHits);
        help.addOption(RESOLVE_EXTERNAL_ID_OPTION, new BooleanParser(), "Whether to print external IDs rather than numerical IDs if available (reduces performance; defaults to true).");
        help.addOption(VERBOSE_OPTION, new BooleanParser(), "Print verbose execution log to stderr; defaults to false).");
    }

    public SolrQuery getSearchQuery() {
        return query;
    }

    public String getIndexName() {
        return indexName;
    }

    public int getMaxHits() {
        return maxHits;
    }

    public boolean getResolveIds() {
        return resolveIds;
    }

    public boolean verbose() {
        return verbose;
    }
}

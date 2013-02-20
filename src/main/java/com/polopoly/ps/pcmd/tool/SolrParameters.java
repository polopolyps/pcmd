package com.polopoly.ps.pcmd.tool;

import org.apache.solr.client.solrj.SolrQuery;

import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.Arguments;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.ps.pcmd.argument.Parameters;
import com.polopoly.ps.pcmd.parser.BooleanParser;
import com.polopoly.ps.pcmd.parser.IntegerParser;
import com.polopoly.util.StringUtil;
import com.polopoly.util.client.PolopolyContext;

public class SolrParameters implements Parameters {
    private static final String INDEX_OPTION = "index";
    private static final String MAX_HITS_OPTION = "maxhits";
    private static final String RESOLVE_EXTERNAL_ID_OPTION = "resolveid";
    private static final String VERBOSE_OPTION = "verbose";
    private static final String INSPECT_OPTION = "inspect";
    private static final String FILTER_QUERY_OPTION = "fq";

    private SolrQuery query;
    private String indexName;
    private Integer maxHits;
    private boolean resolveIds;
    private boolean verbose;
    private boolean inspect;
    private String filterQuery;

    @Override
    public void parseParameters(Arguments args, PolopolyContext context) throws ArgumentException {
    	String tempQuery = "";
    	if (args.getArgumentCount() == 0) {
            throw new ArgumentException("There were no arguments (possibly only options). The search query should be specified as arguments.");
        }
    	
    	for (int i = 0; i < args.getArgumentCount(); i++) {
            String arg = args.getArgument(i);

            tempQuery = tempQuery + " " + arg;
        }
    	query = new SolrQuery(tempQuery);    	
    	
        indexName = args.getOptionString(INDEX_OPTION, "public");
        maxHits = args.getOption(MAX_HITS_OPTION, new IntegerParser(), "" + Integer.MAX_VALUE);
        resolveIds = args.getFlag(RESOLVE_EXTERNAL_ID_OPTION, true);
        verbose = args.getFlag(VERBOSE_OPTION, false);
        inspect = args.getFlag(INSPECT_OPTION, false);
        filterQuery = args.getOptionString(FILTER_QUERY_OPTION,"");
        if(!StringUtil.isEmpty(filterQuery)){

        	if(filterQuery.startsWith("\"")){
        		filterQuery = filterQuery.substring(1);
        	}
        	if(filterQuery.endsWith("\"")){
        		filterQuery = filterQuery.substring(0, filterQuery.length()-2);
        	}

        	query.addFilterQuery(filterQuery);
        }
    }

    @Override
    public void getHelp(ParameterHelp help) {
        help.setArguments(null, "The solr query");
        help.addOption(INDEX_OPTION, null, "The index to search, defaults to public");
        help.addOption(MAX_HITS_OPTION, null, "The maximum number of hits returned, defaults to " + maxHits);
        help.addOption(RESOLVE_EXTERNAL_ID_OPTION, new BooleanParser(), "Whether to print external IDs rather than numerical IDs if available (reduces performance; defaults to true).");
        help.addOption(VERBOSE_OPTION, new BooleanParser(), "Print verbose execution log to stderr; defaults to false).");
        help.addOption(INSPECT_OPTION, new BooleanParser(), "Show stored fields in Solr document according to Solr search result's content ID; defaults to false).");
        help.addOption(FILTER_QUERY_OPTION, null, "The filter query that can be used to restrict the super set of documents that can be returned, without influencing score");
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
    
    public boolean inspect(){
    	return inspect;
    }
    
    public String getFilterQuery(){
    	return filterQuery;
    }
}

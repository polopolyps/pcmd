package com.polopoly.ps.pcmd.tool;

import com.google.common.base.Strings;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.Arguments;
import com.polopoly.ps.pcmd.argument.ContentIdListParameters;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.ps.pcmd.argument.Parameters;
import com.polopoly.util.client.PolopolyContext;

public class ReindexParameters extends ContentIdListParameters implements Parameters {

    private static final String SERVICE_URL = "serviceurl";
    private static final String INDEX_NAME = "index";
    private static final String MAX_IDS = "max";
    private static final String REINDEX_ALL = "reindexall";

    public static final String DEFAULT_SERVICE_URL = "http://localhost:8080/solr-indexer";
    public static final String DEFAULT_INDEX_NAME = "all";
    public static final int DEFAULT_MAX_IDS = 10;
    public static final boolean DEFAULT_REINDEX_ALL = false;

    private String serviceUrl;
    private String indexName;
    private int maxContentIds;
    private boolean reindexAll;

    @Override
    public void parseParameters(final Arguments args, final PolopolyContext context) throws ArgumentException {

        super.parseParameters(args, context);

        String url = args.getOptionString(SERVICE_URL, DEFAULT_SERVICE_URL);
        if (url.endsWith("/")) {
            int len = url.length() - 1;
            if (len < 0) {
                len = 0;
            }
            url = url.substring(0, len);
        }
        if (Strings.isNullOrEmpty(url)) {
            url = DEFAULT_SERVICE_URL;
        }
        setServiceUrl(url);
        setIndexName(args.getOptionString(INDEX_NAME, DEFAULT_INDEX_NAME));
        setMaxContentIds(parseInt(args.getOptionString(MAX_IDS, null), DEFAULT_MAX_IDS));
        setReindexAll(parseBoolean(args.getOptionString(REINDEX_ALL, null), DEFAULT_REINDEX_ALL));

        if (getServiceUrl() == null) {
            throw new ArgumentException("Missing " + SERVICE_URL + " parameter.");
        }
        
    }

    @Override
    public void getHelp(final ParameterHelp help) {
        super.getHelp(help);
        help.addOption(SERVICE_URL, null, "The service url (default is " + DEFAULT_SERVICE_URL + ")");
        help.addOption(INDEX_NAME, null, "The index name (the default is \"" + DEFAULT_INDEX_NAME + "\" and it means all indexes)");
        help.addOption(MAX_IDS, null, "Max number of contents to be indexed at the same time (default is " + DEFAULT_MAX_IDS + ")");
        help.addOption(REINDEX_ALL, null, "Set to true if you want to reindex everything (default is " + DEFAULT_REINDEX_ALL + ")");
    }

    private boolean parseBoolean(final String value, final boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }

        return Boolean.parseBoolean(value);
    }

    private int parseInt(final String value, final int defaultValue) throws ArgumentException {
        if (value == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ArgumentException("Incorrect " + MAX_IDS + " parameter, must be a number.");
        }
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(final String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(final String indexName) {
        this.indexName = indexName;
    }


    public int getMaxContentIds() {
        return maxContentIds;
    }

    public void setMaxContentIds(final int maxContentIds) {
        this.maxContentIds = maxContentIds;
    }

    public boolean isReindexAll() {
        return reindexAll;
    }

    public void setReindexAll(final boolean reindexAll) {
        this.reindexAll = reindexAll;
    }

}

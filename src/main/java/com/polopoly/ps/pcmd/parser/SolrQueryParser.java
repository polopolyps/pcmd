package com.polopoly.ps.pcmd.parser;

import org.apache.solr.client.solrj.SolrQuery;

public class SolrQueryParser implements Parser<SolrQuery> {

    @Override
    public SolrQuery parse(String string) throws ParseException {
        return new SolrQuery(string);
    }

    @Override
    public String getHelp() {
        return "A valid solr query.";
    }

}

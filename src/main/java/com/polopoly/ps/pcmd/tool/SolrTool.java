package com.polopoly.ps.pcmd.tool;

import static org.apache.commons.lang.StringUtils.capitalize;

import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;

import com.polopoly.cm.ContentId;
import com.polopoly.management.ServiceNotAvailableException;
import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.ps.pcmd.field.content.AbstractContentIdField;
import com.polopoly.search.solr.SearchClient;
import com.polopoly.search.solr.SearchResult;
import com.polopoly.search.solr.SearchResultPage;
import com.polopoly.util.client.PolopolyContext;

/**
 * A tool for searching in polopoly solr index
 * 
 */
public class SolrTool implements Tool<SolrParameters>, RequiresSolr {
    private boolean verbose = false;

    @Override
    public void execute(PolopolyContext context, SolrParameters parameters) throws FatalToolException {
        verbose = parameters.verbose();

        String componentName = "search_solrClient" + capitalize(parameters.getIndexName());
        if (verbose) {
            System.err.println("searching index: " + componentName);
        }

        SearchClient searchClient = (SearchClient) context.getApplication().getApplicationComponent(componentName);
        if (searchClient == null) {
            throw new FatalToolException(componentName + " is not available");
        }
        if(context.getPolicyCMServer() == null) {
        	throw new FatalToolException("CMServer is not available");
        }

        try {

            List<ContentId> searchResult = searchContent(searchClient, parameters);
            boolean resolveExternalId = parameters.getResolveIds();

            if (verbose) {
                System.err.println(""); // Make a space after meta info.
            }
            for (ContentId id : searchResult) {
                if (resolveExternalId) {
                    System.out.println(AbstractContentIdField.get(id, context));
                } else {
                    System.out.println(id.getContentId().getContentIdString());
                }
            }
        } catch (Exception e) {
            throw new FatalToolException("Failed to perform search for " + parameters.getSearchQuery(), e);
        }

    }

    private List<ContentId> searchContent(SearchClient searchClient, SolrParameters params) throws SolrServerException, ServiceNotAvailableException {
        if (verbose) {
            System.err.println("search query: " + params.getSearchQuery());
        }
        long t0 = System.currentTimeMillis();
        SearchResult result = searchClient.search(params.getSearchQuery(), params.getMaxHits());
        long t1 = System.currentTimeMillis();
        if (verbose) {
            System.err.println("response time: " + (t1 - t0) + " miliseconds.");
        }
        SearchResultPage searchResultPage = result.getPage(0);
        return searchResultPage.getHits();
    }

    @Override
    public SolrParameters createParameters() {
        return new SolrParameters();
    }

    @Override
    public String getHelp() {
        return "Make a search in solr, return the hits as content ids";
    }


}

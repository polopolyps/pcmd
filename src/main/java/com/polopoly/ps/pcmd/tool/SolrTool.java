package com.polopoly.ps.pcmd.tool;

import static org.apache.commons.lang.StringUtils.capitalize;

import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentIdFactory;
import com.polopoly.management.ServiceNotAvailableException;
import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.ps.pcmd.field.content.AbstractContentIdField;
import com.polopoly.search.solr.SearchClient;
import com.polopoly.search.solr.SearchResult;
import com.polopoly.search.solr.SearchResultPage;
import com.polopoly.search.solr.SolrSearchClient;
import com.polopoly.search.solr.impl.SearchResultImpl;
import com.polopoly.util.StringUtil;
import com.polopoly.util.client.PolopolyContext;

/**
 * A tool for searching in polopoly solr index
 * 
 */
public class SolrTool implements Tool<SolrParameters>, RequiresSolr {
    private boolean verbose = false;
    private boolean inspect = false;
    private boolean resolveExternalId = true;

    @Override
    public void execute(PolopolyContext context, SolrParameters parameters) throws FatalToolException {
        verbose = parameters.verbose();
        inspect = parameters.inspect();
        resolveExternalId = parameters.getResolveIds();

        String componentName = "search_solrClient" + capitalize(parameters.getIndexName());
        if (verbose) {
            System.err.println("searching index: " + componentName);
        }
        
        if(inspect){
        	SolrSearchClient solrSearchClient = (SolrSearchClient) context.getApplication().getApplicationComponent(componentName);
    		if(solrSearchClient == null || context.getPolicyCMServer() == null) {
                StringBuilder sb = new StringBuilder();
                throw new FatalToolException(componentName + " or CMServer is not avaible, available indices are: " + sb.toString());
            }
    		
    		try {
				searchContentWithInspect(solrSearchClient, parameters, context);
    		} catch (SolrServerException e) {
    			System.err.println("Could not generate completely due to SolrServerException"+ e.getMessage());
    		} catch (ServiceNotAvailableException e) {
    			System.err.println("Could not generate completely due to ServiceNotAvailableException"+ e.getMessage());
    		}
        }else{

            SearchClient searchClient = (SearchClient) context.getApplication().getApplicationComponent(componentName);
            if (searchClient == null) {
                throw new FatalToolException(componentName + " is not available");
            }
            if(context.getPolicyCMServer() == null) {
                throw new FatalToolException("CMServer is not available");
            }

	        try {
                List<ContentId> searchResult = searchContent(searchClient, parameters);
                

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

    private void searchContentWithInspect(SolrSearchClient searchClient, SolrParameters params, PolopolyContext context) throws SolrServerException, ServiceNotAvailableException {
        if (verbose) {
            System.err.println("search query: " + params.getSearchQuery());
        }
        long t0 = System.currentTimeMillis();
        SearchResultImpl result = (SearchResultImpl)searchClient.search(params.getSearchQuery(), params.getMaxHits());
        long t1 = System.currentTimeMillis();
        if (verbose) {
            System.err.println("response time: " + (t1 - t0) + " miliseconds.");
        }
        
    
			SearchResultPage resultPage = result.getPage(0);
			if(resultPage != null){
				List<QueryResponse> resps = resultPage.getQueryResponses();
				for(QueryResponse r : resps) {
					SolrDocumentList sdls = r.getResults();						
					for (SolrDocument sd : sdls) {
						String cid = (String) sd.getFieldValue("contentId");
						if(!StringUtil.isEmpty(cid)){
							if (resolveExternalId) {
								ContentId id = ContentIdFactory.createContentId(cid);
		                        System.out.println(AbstractContentIdField.get(id, context));
		                    } else {
		                        System.out.println(cid);
		                    }
						}
						
						for (String field : sd.getFieldNames()) {
					        System.out.println("          "+ field + ": " + sd.getFieldValue(field));
					    }
						
						System.out.println("");
						System.out.println("");
					}
				}
			}
		
       
        
    }

}

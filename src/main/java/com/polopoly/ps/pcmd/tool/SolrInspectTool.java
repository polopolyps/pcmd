package com.polopoly.ps.pcmd.tool;

import static org.apache.commons.lang.StringUtils.capitalize;

import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
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
import com.polopoly.search.solr.SearchResultPage;
import com.polopoly.search.solr.SolrSearchClient;
import com.polopoly.search.solr.impl.SearchResultImpl;
import com.polopoly.util.StringUtil;
import com.polopoly.util.client.PolopolyContext;

public class SolrInspectTool implements Tool<SolrInspectParameters>, RequiresSolr {
    private boolean verbose = false;
    private boolean resolveExternalId = true;


	@Override
	public void execute(PolopolyContext context, SolrInspectParameters  parameters)
			throws FatalToolException {
		verbose = parameters.verbose();
        resolveExternalId = parameters.getResolveIds();
        
		String componentName = "search_solrClient" + capitalize(parameters.getIndexName());
        if (verbose) {
            System.err.println("searching index: " + componentName);
        }
        
        SolrSearchClient solrSearchClient = (SolrSearchClient) context.getApplication().getApplicationComponent(componentName);
		if(solrSearchClient == null || context.getPolicyCMServer() == null) {
            StringBuilder sb = new StringBuilder();
            throw new FatalToolException(componentName + " or CMServer is not avaible, available indices are: " + sb.toString());
        }
		
		Iterator<ContentId> it = parameters.getContentIds();
		while (it.hasNext()) {
			try {
				inspectContentId(solrSearchClient, it.next(), context);
    		} catch (SolrServerException e) {
    			System.err.println("Could not generate completely due to SolrServerException"+ e.getMessage());
    		} catch (ServiceNotAvailableException e) {
    			System.err.println("Could not generate completely due to ServiceNotAvailableException"+ e.getMessage());
    		}
		}
	}

	@Override
	public SolrInspectParameters createParameters() {
		return new SolrInspectParameters();
	}

	@Override
	public String getHelp() {
		return "Return indexed fields of a series of content IDs";
	}
	
	private void inspectContentId(SolrSearchClient searchClient, ContentId contentId, PolopolyContext context) throws SolrServerException, ServiceNotAvailableException {
		String contentIdQuery = "contentId:" + contentId.getContentId().getContentIdString();
		SolrQuery query = new SolrQuery(contentIdQuery); 
		SearchResultImpl result = (SearchResultImpl)searchClient.search(query, Integer.MAX_VALUE);
 
	    
		SearchResultPage resultPage = result.getPage(0);
		if(resultPage != null){
			List<QueryResponse> resps = resultPage.getQueryResponses();
						
			for(QueryResponse r : resps) {
				SolrDocumentList sdls = r.getResults();	
				if(sdls.size() < 1){
					System.out.println("Solr index not found:" + contentId.getContentId().getContentIdString());
				}
				
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
		}else{
			System.out.println("Solr index not found:" + contentId.getContentId().getContentIdString());
		}
	}

}

package com.polopoly.pcmd.application;

import com.polopoly.application.Application;
import com.polopoly.application.IllegalApplicationStateException;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.client.CmClientBase;
import com.polopoly.ps.pcmd.ApplicationComponentProvider;
import com.polopoly.search.solr.SolrIndexName;
import com.polopoly.search.solr.SolrSearchClient;

public abstract class AbstractSolrSearchClientApplicationComponentProvider implements ApplicationComponentProvider {

	@Override
	public final void add(Application appication) throws IllegalApplicationStateException {
		CmClient cmClient = (CmClient) appication.getApplicationComponent(CmClientBase.DEFAULT_COMPONENT_NAME);
		SolrSearchClient searchClient = new SolrSearchClient(SolrSearchClient.DEFAULT_MODULE_NAME, "solrClient" + getIndexName(),
				cmClient);
		searchClient.setIndexName(new SolrIndexName(getIndexName().toLowerCase()));
		appication.addApplicationComponent(searchClient);
	}
	
	abstract String getIndexName();
	
}

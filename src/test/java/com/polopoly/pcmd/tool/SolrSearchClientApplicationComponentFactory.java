package com.polopoly.pcmd.tool;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.application.Application;
import com.polopoly.application.ApplicationComponent;
import com.polopoly.application.ConnectionProperties;
import com.polopoly.application.IllegalApplicationStateException;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.client.CmClientBase;
import com.polopoly.ps.testbase.ApplicationComponentFactory;
import com.polopoly.search.solr.SolrIndexName;
import com.polopoly.search.solr.SolrSearchClient;


public class SolrSearchClientApplicationComponentFactory implements ApplicationComponentFactory {

	public static final Logger logger = Logger.getLogger(SolrSearchClientApplicationComponentFactory.class.getName());
	
	@Override
	public ApplicationComponent create(Application application,
			ConnectionProperties connectionProperties) {

        CmClient cmClient = (CmClient) application.getApplicationComponent(CmClientBase.DEFAULT_COMPOUND_NAME);
        
		SolrSearchClient solrSearchClient =
                new SolrSearchClient(SolrSearchClient.DEFAULT_MODULE_NAME,
                        SolrSearchClient.DEFAULT_COMPONENT_NAME,
                        cmClient);
		try {
			solrSearchClient.setIndexName(new SolrIndexName("public"));
			application.addApplicationComponent(solrSearchClient);			
		} catch (IllegalArgumentException e) {
			logger.log(Level.SEVERE, "Failed to create SolrSearchClient application component!", e);
		} catch (IllegalApplicationStateException e) {
			logger.log(Level.SEVERE, "Failed to create SolrSearchClient application component!", e);
		}
		
		return solrSearchClient;
	}

}

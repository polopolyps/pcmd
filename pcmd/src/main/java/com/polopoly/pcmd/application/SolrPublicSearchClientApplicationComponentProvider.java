package com.polopoly.pcmd.application;

import com.polopoly.application.Application;
import com.polopoly.application.IllegalApplicationStateException;
import com.polopoly.ps.pcmd.ApplicationComponentProvider;

public class SolrPublicSearchClientApplicationComponentProvider extends AbstractSolrSearchClientApplicationComponentProvider {

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SolrPublicSearchClientApplicationComponentProvider");
		return builder.toString();
	}

	@Override
	String getIndexName() {
		return "Public";
	}
	

}

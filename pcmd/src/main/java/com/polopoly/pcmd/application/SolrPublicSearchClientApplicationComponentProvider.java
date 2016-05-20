package com.polopoly.pcmd.application;

public class SolrPublicSearchClientApplicationComponentProvider extends AbstractSolrSearchClientApplicationComponentProvider {

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SolrPublicSearchClientApplicationComponentProvider");
		return builder.toString();
	}

	@Override
	public String getIndexName() {
		return "Public";
	}
	

}

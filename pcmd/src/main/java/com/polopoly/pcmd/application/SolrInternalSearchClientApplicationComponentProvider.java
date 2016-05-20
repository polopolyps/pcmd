package com.polopoly.pcmd.application;

public class SolrInternalSearchClientApplicationComponentProvider extends AbstractSolrSearchClientApplicationComponentProvider {

	@Override
	public String getIndexName() {
		return "Internal";
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SolrInternalSearchClientApplicationComponentProvider");
		return builder.toString();
	}


	

}

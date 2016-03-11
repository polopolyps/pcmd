package com.polopoly.pcmd.application;

public class SolrInternalSearchClientApplicationComponentProvider extends AbstractSolrSearchClientApplicationComponentProvider {

	@Override
	String getIndexName() {
		return "Internal";
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SolrInternalSearchClientApplicationComponentProvider");
		return builder.toString();
	}


	

}

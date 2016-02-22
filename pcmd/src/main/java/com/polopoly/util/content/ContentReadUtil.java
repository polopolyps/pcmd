package com.polopoly.util.content;

import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.contentid.ContentIdUtil;
import com.polopoly.util.contentlist.ContentListUtil;
import com.polopoly.util.exception.NoExternalIdSetException;

public interface ContentReadUtil extends RuntimeExceptionContent {
	PolopolyContext getContext();

	PolicyCMServer getPolicyCMServer();

	ContentListUtil getContentList();

	ContentListUtil getContentList(String contentList);

	String getContentIdString();

	/**
	 * May return null.
	 */
	ExternalContentId getExternalId();

	String getExternalIdString() throws NoExternalIdSetException;

	ContentIdUtil getContentId();
}

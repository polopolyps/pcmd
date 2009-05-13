package com.polopoly.util.content;

import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.contentlist.ContentListUtil;

public interface ContentReadUtil extends RuntimeExceptionContent {
    PolopolyContext getContext();
    PolicyCMServer getPolicyCMServer();
    ContentListUtil getContentList();
    ContentListUtil getContentList(String contentList);
    String getContentIdString();
}

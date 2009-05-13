package com.polopoly.util.content;

import com.polopoly.cm.client.Content;
import com.polopoly.util.contentid.ContentIdUtil;
import com.polopoly.util.contentid.ContentIterable;

public interface ContentUtil extends ContentReadUtil, RuntimeExceptionContent, Content {
    ContentIdUtil getContentReference(String groupName, String name);
    ContentIdUtil getInputTemplateId();
    ContentIdUtil getSecurityParentId();
    ContentIterable getSecurityParentChain();
}
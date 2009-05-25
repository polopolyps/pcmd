package com.polopoly.util.content;

import com.polopoly.cm.client.Content;
import com.polopoly.util.contentid.ContentIdUtil;
import com.polopoly.util.contentid.ContentIterable;
import com.polopoly.util.policy.InputTemplateUtil;

public interface ContentUtil extends ContentReadUtil, RuntimeExceptionContent, Content {
    ContentIdUtil getContentReference(String groupName, String name);
    ContentIdUtil getInputTemplateId();
    InputTemplateUtil getInputTemplate();
    ContentIdUtil getSecurityParentId();
    ContentIterable getSecurityParentChain();
}
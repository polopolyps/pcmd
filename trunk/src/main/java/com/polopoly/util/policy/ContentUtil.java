package com.polopoly.util.policy;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.Content;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.policy.PolicyCMServer;

public class ContentUtil {
    private Content content;
    private PolicyCMServer server;

    /**
     * Use {@link Util#util(ContentRead, PolicyCMServer)} to get an instance.
     */
    ContentUtil(ContentRead content, PolicyCMServer server) {
        this.content = (Content) content;
        this.server = server;
    }

    public ContentListUtil getContentList() {
        try {
            return new ContentListUtil(content.getContentList(), this, server);
        } catch (CMException e) {
            throw new CMRuntimeException("While getting default content list in " + this + ": " + e.getMessage(), e);
        }
    }

    public ContentListUtil getContentList(String contentList) {
        try {
            return new ContentListUtil(content.getContentList(contentList), this, server);
        } catch (CMException e) {
            throw new CMRuntimeException("While getting content list \"" + contentList + "\" in " + this + ": " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return content.getContentId().getContentId().getContentIdString();
    }
}

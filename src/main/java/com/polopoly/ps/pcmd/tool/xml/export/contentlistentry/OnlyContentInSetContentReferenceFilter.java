package com.polopoly.ps.pcmd.tool.xml.export.contentlistentry;

import java.util.Set;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.ContentRead;

public class OnlyContentInSetContentReferenceFilter implements ContentReferenceFilter {
    protected Set<ContentId> allowedReferences;

    public OnlyContentInSetContentReferenceFilter(Set<ContentId> allowedReferences) {
        this.allowedReferences = allowedReferences;
    }

    public boolean isAllowed(ContentRead inContent, ContentId referredContent) {
        return allowedReferences.contains(referredContent.getContentId());
    }
}

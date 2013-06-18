package com.polopoly.ps.pcmd.tool.xml.export.contentlistentry;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.ContentRead;

public class OrContentReferenceFilter implements ContentReferenceFilter {
    private ContentReferenceFilter[] filters;

    public OrContentReferenceFilter(ContentReferenceFilter... filters) {
        this.filters = filters;
    }

    public boolean isAllowed(ContentRead inContent, ContentId referredContent) {
        for (ContentReferenceFilter filter : filters) {
            if (filter.isAllowed(inContent, referredContent)) {
                return true;
            }
        }

        return false;
    }
}

package com.polopoly.ps.pcmd.tool.xml.export.contentlistentry;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.util.ContentIdFilter;

public class ContentIdFilterToContentReferenceFilterWrapper implements ContentReferenceFilter {
    private ContentIdFilter contentIdFilter;

    public ContentIdFilterToContentReferenceFilterWrapper(ContentIdFilter contentIdFilter) {
        this.contentIdFilter = contentIdFilter;
    }

    public boolean isAllowed(ContentRead inContent, ContentId referredContent) {
        return contentIdFilter.accept(referredContent);
    }

}

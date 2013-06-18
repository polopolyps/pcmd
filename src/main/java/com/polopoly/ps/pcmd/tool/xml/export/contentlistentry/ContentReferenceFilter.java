package com.polopoly.ps.pcmd.tool.xml.export.contentlistentry;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.ContentRead;

public interface ContentReferenceFilter {

    boolean isAllowed(ContentRead inContent, ContentId referredContent);

}

package com.polopoly.ps.pcmd.tool.xml.export.filteredcontent;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.util.ContentIdFilter;

public class ExcludeMetadataVersionFilter implements ContentIdFilter {

    @SuppressWarnings("deprecation")
    public boolean accept(ContentId contentId) {
        return contentId.getVersion() != VersionedContentId.META_DATA_VERSION;
    }

}

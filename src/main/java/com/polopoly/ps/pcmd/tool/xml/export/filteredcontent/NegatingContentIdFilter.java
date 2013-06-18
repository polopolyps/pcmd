package com.polopoly.ps.pcmd.tool.xml.export.filteredcontent;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.util.ContentIdFilter;

public class NegatingContentIdFilter implements ContentIdFilter {

    private ContentIdFilter delegate;

    public NegatingContentIdFilter(ContentIdFilter delegate) {
        this.delegate = delegate;
    }

    public boolean accept(ContentId contentId) {
        return !delegate.accept(contentId);
    }

}

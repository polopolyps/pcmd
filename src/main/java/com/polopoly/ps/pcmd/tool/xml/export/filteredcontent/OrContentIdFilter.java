package com.polopoly.ps.pcmd.tool.xml.export.filteredcontent;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.util.ContentIdFilter;

public class OrContentIdFilter implements ContentIdFilter {

    private ContentIdFilter[] delegates;

    public OrContentIdFilter(ContentIdFilter... delegates) {
        this.delegates = delegates;
    }

    public boolean accept(ContentId contentId) {
        for (ContentIdFilter delegate : delegates) {
            if (delegate.accept(contentId)) {
                return true;
            }
        }

        return false;
    }

}

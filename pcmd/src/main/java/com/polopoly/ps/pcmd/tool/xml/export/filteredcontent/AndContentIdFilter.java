package com.polopoly.ps.pcmd.tool.xml.export.filteredcontent;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.util.ContentIdFilter;

public class AndContentIdFilter implements ContentIdFilter {

    private ContentIdFilter[] delegates;

    public AndContentIdFilter(ContentIdFilter... delegates) {
        this.delegates = delegates;
    }

    public boolean accept(ContentId contentId) {
        for (ContentIdFilter delegate : delegates) {
            if (!delegate.accept(contentId)) {
                return false;
            }
        }

        return true;
    }

}

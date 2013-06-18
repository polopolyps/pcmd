package com.polopoly.ps.pcmd.tool.xml.export.filteredcontent;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.util.ContentIdFilter;

public class AcceptAllContentIdFilter implements ContentIdFilter {

    public boolean accept(ContentId contentId) {
        return true;
    }

}

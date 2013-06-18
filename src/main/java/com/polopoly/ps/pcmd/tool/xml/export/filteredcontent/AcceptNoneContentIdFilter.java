package com.polopoly.ps.pcmd.tool.xml.export.filteredcontent;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.util.ContentIdFilter;

public class AcceptNoneContentIdFilter implements ContentIdFilter {

    public boolean accept(ContentId contentId) {
        return false;
    }

}

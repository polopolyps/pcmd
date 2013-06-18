package com.polopoly.ps.pcmd.tool.xml.export.filteredcontent;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.util.ContentIdFilter;
import com.polopoly.ps.pcmd.client.Major;

public class MajorFilter implements ContentIdFilter {
    private Major majorToInclude;

    public MajorFilter(Major majorToInclude) {
        this.majorToInclude = majorToInclude;
    }

    public boolean accept(ContentId contentId) {
        return contentId.getMajor() == majorToInclude.getIntegerMajor();
    }

}

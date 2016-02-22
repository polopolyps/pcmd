package com.polopoly.ps.pcmd.tool.xml.export.filteredcontent;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.util.ContentIdFilter;

public class SecurityRootDepartmentFilter implements ContentIdFilter {
    private static final ContentId SECURITY_ROOT_ID = new ContentId(2, 10);

    public boolean accept(ContentId contentId) {
        // the security root dept doesn't have an external ID for some reason.
        return SECURITY_ROOT_ID.equalsIgnoreVersion(contentId);
    }

}

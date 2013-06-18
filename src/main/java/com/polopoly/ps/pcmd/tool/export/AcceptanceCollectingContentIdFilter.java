package com.polopoly.ps.pcmd.tool.export;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.util.ContentIdFilter;

public class AcceptanceCollectingContentIdFilter extends AbstractCollectingContentIdFilter {

    public AcceptanceCollectingContentIdFilter(ContentIdFilter delegate) {
        super(delegate);
    }

    @Override
    protected boolean shouldCollect(ContentId contentId, boolean result) {
        return result;
    }

}

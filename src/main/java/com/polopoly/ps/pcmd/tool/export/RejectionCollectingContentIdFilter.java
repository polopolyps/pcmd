package com.polopoly.ps.pcmd.tool.export;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.util.ContentIdFilter;

public class RejectionCollectingContentIdFilter extends AbstractCollectingContentIdFilter {

    public RejectionCollectingContentIdFilter(ContentIdFilter delegate) {
        super(delegate);
    }

    @Override
    protected boolean shouldCollect(ContentId contentId, boolean result) {
        return !result;
    }

}

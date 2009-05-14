package com.polopoly.pcmd.tool;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentReference;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.contentid.ContentIdUtil;
import com.polopoly.util.policy.Util;

public class ContentReferenceUtil extends ContentReference {
    private PolopolyContext context;

    public ContentReferenceUtil(ContentId referredContentId,
            ContentId referenceMetaDataId, PolopolyContext context) {
        super(referredContentId, referenceMetaDataId);

        this.context = context;
    }

    public ContentReferenceUtil(ContentReference entry, PolopolyContext context) {
        this(entry.getReferredContentId(), entry.getReferenceMetaDataId(), context);
    }

    @Override
    public ContentIdUtil getReferenceMetaDataId() {
        return Util.util(super.getReferenceMetaDataId(), context);
    }

    @Override
    public ContentIdUtil getReferredContentId() {
        return Util.util(super.getReferredContentId(), context);
    }

}

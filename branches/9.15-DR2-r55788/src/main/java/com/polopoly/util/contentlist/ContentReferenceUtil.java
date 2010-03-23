package com.polopoly.util.contentlist;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentReference;
import com.polopoly.cm.collections.ContentListRead;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.contentid.ContentIdUtil;
import com.polopoly.util.exception.NoReferenceMetaDataException;
import com.polopoly.util.exception.PolicyGetException;
import com.polopoly.util.policy.Util;

public class ContentReferenceUtil extends ContentReference {
    private PolopolyContext context;

    private ContentListRead contentList;

    public ContentReferenceUtil(ContentId referredContentId,
            ContentId referenceMetaDataId, ContentListRead contentList,
            PolopolyContext context) {
        super(referredContentId, referenceMetaDataId);

        if (context == null) {
            throw new IllegalArgumentException("Context was null.");
        }

        this.contentList = contentList;
        this.context = context;
    }

    public ContentReferenceUtil(ContentReference entry,
            ContentListRead contentList, PolopolyContext context) {
        this(entry.getReferredContentId(), entry.getReferenceMetaDataId(),
                contentList, context);
    }

    public <T> T getMetaData(Class<T> policyClass)
            throws NoReferenceMetaDataException, PolicyGetException {
        ContentId referenceMetaDataId = super.getReferenceMetaDataId();

        if (referenceMetaDataId == null) {
            throw new NoReferenceMetaDataException(
                    "No reference metadata available for " + this + ".");
        }

        return context.getPolicy(referenceMetaDataId, policyClass);
    }

    @Override
    public ContentIdUtil getReferenceMetaDataId() {
        return Util.util(super.getReferenceMetaDataId(), context);
    }

    @Override
    public ContentIdUtil getReferredContentId() {
        return Util.util(super.getReferredContentId(), context);
    }

    @Override
    public String toString() {
        return "reference to "
                + Util.util(getReferredContentId(), context)
                + (getReferenceMetaDataId() != null ? " with metadata "
                        + Util.util(getReferenceMetaDataId(), context) : "")
                + (contentList instanceof ContentListUtil ? " in "
                        + contentList.toString() : "");
    }
}

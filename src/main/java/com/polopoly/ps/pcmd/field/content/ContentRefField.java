package com.polopoly.ps.pcmd.field.content;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.ps.pcmd.util.ContentReference;
import com.polopoly.util.client.PolopolyContext;

public class ContentRefField extends AbstractContentIdField {
    private String group;
    private String reference;

    public ContentRefField(String group, String reference) {
        this.group = group;
        this.reference = reference;
    }

    public ContentRefField(ContentReference referenceObject) {
        this.group = referenceObject.getGroup();
        this.reference = referenceObject.getReference();
    }

    @Override
    protected ContentId getContentId(ContentRead content,
            PolopolyContext context) throws CMException {
        return content.getContentReference(group, reference);
    }
}

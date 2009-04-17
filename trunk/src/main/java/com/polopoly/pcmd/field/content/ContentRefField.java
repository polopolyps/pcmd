package com.polopoly.pcmd.field.content;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.pcmd.tool.PolopolyContext;
import com.polopoly.pcmd.util.ContentReference;

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

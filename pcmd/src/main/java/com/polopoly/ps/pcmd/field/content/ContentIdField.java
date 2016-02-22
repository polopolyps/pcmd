package com.polopoly.ps.pcmd.field.content;

import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.util.client.PolopolyContext;

public class ContentIdField implements Field {

    public String get(ContentRead content, PolopolyContext context) {
        ExternalContentId externalId;

        try {
            externalId = content.getExternalId();
        } catch (CMException e) {
            System.err.println(content.getContentId().getContentIdString() + ": " + e);
            externalId = null;
        }

        if (externalId != null) {
            return externalId.getExternalId();
        }
        else {
            return content.getContentId().getContentIdString();
        }
    }
}

package com.polopoly.pcmd.field;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.pcmd.tool.PolopolyContext;

public abstract class AbstractContentIdField implements Field {
    protected abstract ContentId getContentId(ContentRead content, PolopolyContext context) throws CMException;

    public String get(ContentRead content, PolopolyContext context) {
        ContentId contentId;

        try {
            contentId = getContentId(content, context);
        } catch (CMException e) {
            contentId = null;
            System.err.println(e.toString());
        }

        return get(contentId, context);
    }

    public static String get(ContentId contentId, PolopolyContext context) {
        if (contentId == null) {
            return "";
        }
        else {
            try {
                ContentRead referred = context.getPolicyCMServer().getContent(contentId);

                ExternalContentId externalId = referred.getExternalId();

                if (externalId != null) {
                    if (contentId instanceof VersionedContentId &&
                         ((VersionedContentId) contentId).getVersion() != VersionedContentId.UNDEFINED_VERSION) {
                             return externalId.getExternalId() + '.' +
                                 Integer.toString(((VersionedContentId) contentId).getVersion());
                    }
                    else {
                        return externalId.getExternalId();
                    }
                }
            }
            catch (CMException e) {
                System.err.println(contentId.getContentIdString() + ": " + e.toString());
            }

            return contentId.getContentIdString();
        }
    }
}

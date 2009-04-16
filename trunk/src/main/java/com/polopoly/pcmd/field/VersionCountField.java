package com.polopoly.pcmd.field;

import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.pcmd.tool.PolopolyContext;

public class VersionCountField implements Field {

    public String get(ContentRead content, PolopolyContext context) {
        try {
            return Integer.toString(get(
                    new VersionedContentId(
                            content.getContentId().getContentId(),
                            content.getContentId().getVersion()), context));
        } catch (CMException e) {
            System.err.println(e.toString());

            return "";
        }
    }

    private int get(VersionedContentId versionedContentId, PolopolyContext context) throws CMException {
        int previous =
            context.getPolicyCMServer().getContent(versionedContentId).getVersionInfo().getPreviousVersion();

        if (previous > 0) {
            return get(new VersionedContentId(versionedContentId.getContentId(), previous), context) + 1;
        }
        else {
            return 1;
        }
    }

}

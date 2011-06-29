package com.polopoly.ps.pcmd.tool;

import java.util.Iterator;

import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.Content;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.ps.pcmd.argument.ContentIdListParameters;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.ContentIdToContentIterator;

public class DeleteVersionTool implements Tool<ContentIdListParameters> {
    public ContentIdListParameters createParameters() {
        return new ContentIdListParameters();
    }

    public void execute(PolopolyContext context,
            ContentIdListParameters parameters) {
        Iterator<ContentRead> it =
            new ContentIdToContentIterator(context,
                    parameters.getContentIds(), parameters.isStopOnException());

        while (it.hasNext()) {
            ContentRead next = it.next();

            try {
                VersionedContentId id = next.getContentId();
                ((Content) next).remove();

                System.out.println(id.getContentIdString());
            } catch (CMException e) {
                String errorString = "While removing " +
                    next.getContentId().getContentIdString() + ": " + e;

                if (parameters.isStopOnException()) {
                    throw new CMRuntimeException(errorString, e);
                }
                else {
                    System.err.println(errorString);
                }
            }
        }
    }

    public String getHelp() {
        return "Deletes the specified content version (if a versioned content ID was specified, otherwise the default version is deleted).";
    }
}

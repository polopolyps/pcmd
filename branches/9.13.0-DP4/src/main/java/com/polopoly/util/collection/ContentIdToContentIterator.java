package com.polopoly.util.collection;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.util.client.PolopolyContext;

public class ContentIdToContentIterator extends AbstractContentIdIterator<ContentRead> {
    private static final Logger logger =
        Logger.getLogger(ContentIdToContentIterator.class.getName());

    public ContentIdToContentIterator(PolopolyContext context, Iterator<ContentId> contentIds, boolean stopOnException) {
        super(context, contentIds, stopOnException);
    }

    public ContentIdToContentIterator(PolicyCMServer server, Iterator<ContentId> contentIds) {
        super(server, contentIds);
    }

    @Override
    protected ContentRead fetch() {
        while (it.hasNext()) {
            ContentId contentId = it.next();
            try {
                ContentRead result = server.getContent(contentId);

                count++;

                return result;
            } catch (CMException e) {
                if (stopOnException) {
                    throw new CMRuntimeException(e);
                }
                else {
                    logger.log(Level.WARNING, "While fetching " +
                        contentId.getContentIdString() + " in " + it + ": " + e.toString());
                }
            }
        }

        return null;
    }
}

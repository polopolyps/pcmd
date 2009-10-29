package com.polopoly.util.collection;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.util.client.PolopolyContext;

public class ContentIdToPolicyIterator extends AbstractContentIdIterator<Policy> {
    private static final Logger logger =
        Logger.getLogger(ContentIdToPolicyIterator.class.getName());

    public ContentIdToPolicyIterator(PolopolyContext context, Iterator<? extends ContentId> contentIds, boolean stopOnException) {
        super(context, contentIds, stopOnException);
    }

    public ContentIdToPolicyIterator(PolopolyContext context, Iterator<? extends ContentId> contentIds) {
        super(context, contentIds);
    }

    public ContentIdToPolicyIterator(PolicyCMServer server, Iterator<? extends ContentId> contentIds) {
        super(server, contentIds);
    }

    @Override
    protected Policy fetch() {
        while (it.hasNext()) {
            ContentId contentId = it.next();
            try {
                Policy result = server.getPolicy(contentId);

                count++;

                return result;
            } catch (CMException e) {
                if (stopOnException) {
                    throw new CMRuntimeException(e);
                }
                else {
                    logger.log(Level.WARNING,
                        "While fetching " + contentId.getContentIdString() + " in " + it + ": " + e.toString());
                }
            }
        }

        return null;
    }
}

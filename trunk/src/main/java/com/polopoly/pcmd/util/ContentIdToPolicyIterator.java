package com.polopoly.pcmd.util;

import java.util.Iterator;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.pcmd.tool.PolopolyContext;

public class ContentIdToPolicyIterator extends AbstractContentIdIterator<Policy> {

    public ContentIdToPolicyIterator(PolopolyContext context, Iterator<ContentId> contentIds, boolean stopOnException) {
        super(context, contentIds, stopOnException);
    }

    public ContentIdToPolicyIterator(PolicyCMServer server, Iterator<ContentId> contentIds) {
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
                    System.err.println("While fetching " + contentId.getContentIdString() + ": " + e.toString());
                }
            }
        }

        return null;
    }
}

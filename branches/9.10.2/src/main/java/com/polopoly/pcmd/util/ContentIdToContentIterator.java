package com.polopoly.pcmd.util;

import java.io.PrintStream;
import java.util.Iterator;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.pcmd.tool.PolopolyContext;

public class ContentIdToContentIterator extends FetchingIterator<ContentRead> {
    private Iterator<ContentId> it;
    private PolicyCMServer server;
    private boolean stopOnException;
    private long startTime;
    private int count;

    public ContentIdToContentIterator(PolopolyContext context, Iterator<ContentId> contentIds, boolean stopOnException) {
        this(context.getPolicyCMServer(), contentIds);

        this.stopOnException = stopOnException;
    }

    public ContentIdToContentIterator(PolicyCMServer server, Iterator<ContentId> contentIds) {
        this.it = contentIds;
        this.server = server;

        startTime = System.currentTimeMillis();
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
                    System.err.println("While fetching " + contentId.getContentIdString() + ": " + e.toString());
                }
            }
        }

        return null;
    }

    public void setStopOnException(boolean stopOnException) {
        this.stopOnException = stopOnException;
    }

    public boolean isStopOnException() {
        return stopOnException;
    }

    public void printInfo(PrintStream out) {
        if (count > 10) {
            out.println(count + " content object(s) in " + Math.round((System.currentTimeMillis() - startTime)/1000) + " s.");
        }
    }
}

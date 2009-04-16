package com.polopoly.pcmd.util;

import java.io.PrintStream;
import java.util.Iterator;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.pcmd.tool.PolopolyContext;
import com.polopoly.util.collection.FetchingIterator;

public abstract class AbstractContentIdIterator<T> extends FetchingIterator<T> {
    protected Iterator<ContentId> it;
    protected PolicyCMServer server;
    protected boolean stopOnException;
    protected long startTime;
    protected int count;

    protected AbstractContentIdIterator(PolopolyContext context, Iterator<ContentId> contentIds, boolean stopOnException) {
        this(context.getPolicyCMServer(), contentIds);

        this.stopOnException = stopOnException;
    }

    protected AbstractContentIdIterator(PolicyCMServer server, Iterator<ContentId> contentIds) {
        this.it = contentIds;
        this.server = server;

        startTime = System.currentTimeMillis();
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

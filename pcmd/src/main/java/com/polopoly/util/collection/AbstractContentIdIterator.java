package com.polopoly.util.collection;

import java.io.PrintStream;
import java.util.Iterator;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.util.client.PolopolyContext;

public abstract class AbstractContentIdIterator<T> extends FetchingIterator<T> {
    protected Iterator<? extends ContentId> it;
    protected PolicyCMServer server;
    protected boolean stopOnException;
    protected long startTime;
    protected int count;
	private PolopolyContext context;

    protected AbstractContentIdIterator(PolopolyContext context, Iterator<? extends ContentId> contentIds) {
        this(context, contentIds, false);
        if(context == null) {
        		throw new IllegalArgumentException("Context cannot be null");
        }
		this.context = context;
    }

    protected AbstractContentIdIterator(PolopolyContext context, Iterator<? extends ContentId> contentIds, boolean stopOnException) {
        this(context.getPolicyCMServer(), contentIds);
        this.context = context;
        this.stopOnException = stopOnException;
    }

    protected AbstractContentIdIterator(PolicyCMServer server, Iterator<? extends ContentId> contentIds) {
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
			if (context != null) {
				context.getLogger().info(count + " content object(s) in " + Math.round((System.currentTimeMillis() - startTime) / 1000) + " s.");
			}

		}
	}
}

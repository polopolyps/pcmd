package com.polopoly.util.collection;

import java.util.Iterator;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.policy.Util;

public class ContentIdToContentUtilIterator extends TransformingIterator<ContentRead, ContentUtil>{
    private PolicyCMServer server;

    public ContentIdToContentUtilIterator(PolopolyContext context, Iterator<? extends ContentId> contentIds, boolean stopOnException) {
        super(new ContentIdToContentIterator(context, contentIds, stopOnException));

        this.server = context.getPolicyCMServer();
    }

    public ContentIdToContentUtilIterator(PolicyCMServer server, Iterator<? extends ContentId> contentIds) {
        super(new ContentIdToContentIterator(server, contentIds));

        this.server = server;
    }

    @Override
    protected ContentUtil transform(ContentRead content) {
        return Util.util(content, server);
    };
}

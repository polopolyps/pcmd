package com.polopoly.util.contentid;

import java.util.Iterator;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.policy.Policy;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.CheckedContentIdToPolicyIterator;
import com.polopoly.util.collection.ContentIdToContentUtilIterator;
import com.polopoly.util.collection.ContentIdToPolicyIterator;
import com.polopoly.util.content.ContentUtil;

public class ContentIterable implements Iterable<Policy> {
    private Iterable<ContentIdUtil> contentIdIterable;
    private PolopolyContext context;

    public ContentIterable(PolopolyContext context, Iterable<ContentIdUtil> contentIdIterable) {
        this.contentIdIterable = contentIdIterable;
        this.context = context;
    }

    public Iterator<Policy> iterator() {
        return new ContentIdToPolicyIterator(context, contentIds().iterator());
    }

    public <T extends Policy> Iterable<T> policies(final Class<T> policyClass) {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return new CheckedContentIdToPolicyIterator<T>(context, contentIds().iterator(), policyClass, false) {
                    @Override
                    public String toString() {
                        return contentIds().toString();
                    }
                };
            }
        };
    }

    public Iterable<ContentIdUtil> contentIds() {
        return contentIdIterable;
    }

    public Iterable<ContentUtil> contents() {
        return new Iterable<ContentUtil>() {
            public Iterator<ContentUtil> iterator() {
                return new ContentIdToContentUtilIterator(context, contentIds().iterator(), false);
            }
        };
    }

    public boolean contains(Policy policy) {
        return contains(policy.getContentId());
    }

    public boolean contains(ContentRead content) {
        return contains(content.getContentId());
    }

    public boolean contains(ContentId contentId) {
        for (ContentId childId : contentIds()) {
            if (childId.equalsIgnoreVersion(contentId)) {
                return true;
            }
        }

        return false;
    }
}

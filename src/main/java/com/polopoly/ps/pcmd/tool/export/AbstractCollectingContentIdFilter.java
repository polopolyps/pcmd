package com.polopoly.ps.pcmd.tool.export;

import java.util.HashSet;
import java.util.Set;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.util.ContentIdFilter;
import com.polopoly.ps.pcmd.field.content.AbstractContentIdField;
import com.polopoly.util.client.PolopolyContext;

abstract class AbstractCollectingContentIdFilter implements ContentIdFilter {
    private Set<ContentId> collected = new HashSet<ContentId>(100);
    private ContentIdFilter delegate;

    public AbstractCollectingContentIdFilter(
            ContentIdFilter delegate) {
        this.delegate = delegate;
    }

    public boolean accept(ContentId contentId) {
        boolean result = delegate.accept(contentId);

        if (shouldCollect(contentId, result)) {
            collected.add(contentId);
        }

        return result;
    }

    protected abstract boolean shouldCollect(ContentId contentId, boolean result);

    public Set<ContentId> getCollectedIds() {
        return collected;
    }

    public void printCollectedObjects(PolopolyContext context) {
        if (collected.size() > 50) {
            System.out.print("Some of the objects were: ");

            collected = collect(collected, 50);
        }
        else {
            System.out.print("The objects were: ");
        }

        for (ContentId contentId : collected) {
            System.out.println(AbstractContentIdField.get(contentId, context));
        }
    }

    private <T> Set<T> collect(Set<T> set, int count) {
        HashSet<T> result = new HashSet<T>(count);

        for (T object : set) {
            result.add(object);

            if (result.size() >= count) {
                break;
            }
        }

        return result;
    }
}

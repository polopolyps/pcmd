package com.polopoly.util.collection;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentReference;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.collections.ContentList;

public class ContentListListAdapter extends AbstractList<ContentId> implements List<ContentId> {
    private ContentList contentList;
    private Object toString;

    public ContentListListAdapter(ContentList contentList, Object toString) {
        this.contentList = contentList;
        this.toString = toString;
    }

    @Override
    public void add(int index, ContentId contentId) {
        try {
            contentList.add(index, new ContentReference(contentId, null));
        }
        catch (CMException e) {
            throw new CMRuntimeException(
                "While adding " + contentId + " to " + this + ".");
        }
    }

    @Override
    public boolean add(ContentId contentId) {
        add(size(), contentId);

        return true;
    }

    public void remove(ContentId contentId) {
        for (int i = contentList.size()-1; i >= 0; i--) {
            if (get(i).equalsIgnoreVersion(contentId)) {
                remove(i);
            }
        }
    }

    @Override
    public ContentId remove(int index) {
        ContentId result = get(index);
        contentList.remove(index);

        return result;
    }

    @Override
    public int size() {
        return contentList.size();
    }

    @Override
    public ContentId get(int i) {
        try {
            return contentList.getEntry(i).getReferredContentId();
        } catch (CMException e) {
            throw new CMRuntimeException(
                "While getting entry " + i + " in " + toString() +
                    ": " + e.getMessage(), e);
        }
    }

    @Override
    public Iterator<ContentId> iterator() {
        return new ContentListIterator(contentList) {
            @Override
            public String toString() {
                return ContentListListAdapter.this.toString();
            }
        };
    }

    @Override
    public ContentId set(int index, ContentId id) {
        try {
            contentList.setEntry(index, new ContentReference(id, null));
        } catch (CMException e) {
            throw new CMRuntimeException(
                "While setting entry " + index + " in " + this + ": " + e.getMessage(), e);
        }
        return id;
    }

    @Override
    public String toString() {
        if (toString == null) {
            return "content list";
        }
        else {
            return toString.toString();
        }
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        for (int j = toIndex-fromIndex; j >= 0; j--) {
            contentList.remove(fromIndex);
        }
    }
}

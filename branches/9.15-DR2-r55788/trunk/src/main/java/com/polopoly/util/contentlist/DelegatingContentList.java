package com.polopoly.util.contentlist;

import java.util.List;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentReference;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.collections.ContentList;
import com.polopoly.cm.collections.ContentListRead;
import com.polopoly.util.CheckedCast;
import com.polopoly.util.CheckedClassCastException;

public class DelegatingContentList extends DelegatingContentListRead implements ContentList {
    public DelegatingContentList(ContentListRead delegate) {
        super(delegate);
    }

    private ContentList getWritableContentList() {
        try {
            return CheckedCast.cast(getDelegate(), ContentList.class);
        } catch (CheckedClassCastException e) {
            throw new CMRuntimeException("The supplied content list object for content list " + this +
                " was not a Content, but only a ContentRead. It is therefore not writable.");
        }
    }

    @Deprecated
    public ContentId add(int index, ContentId referredContentId,
            boolean createTocEntry) throws CMException {
        return getWritableContentList().add(index, referredContentId, createTocEntry);
    }

    public void add(int index, ContentReference contentRef) throws CMException {
        getWritableContentList().add(index, contentRef);
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    public List<ContentId> add(int index, List referredContentIds, boolean createTocEntries)
            throws CMException {
        return getWritableContentList().add(index, referredContentIds, createTocEntries);
    }

    @SuppressWarnings("unchecked")
    public void add(int index, List contentRefs) throws CMException {
        getWritableContentList().add(index, contentRefs);
    }

    public boolean allowAddToFullList() throws CMException {
        return getWritableContentList().allowAddToFullList();
    }

    public void move(int oldIndex, int newIndex) throws CMException {
        getWritableContentList().move(oldIndex, newIndex);
    }

    public void rearrange(int[] newOrder) throws CMException {
        getWritableContentList().rearrange(newOrder);
    }

    public Object remove(int index) {
        return getWritableContentList().remove(index);
    }

    public void remove(int[] indices) throws CMException {
        getWritableContentList().remove(indices);
    }

    public void rename(String newContentReferenceGroupName) throws CMException {
        getWritableContentList().rename(newContentReferenceGroupName);
    }

    public void setAllowAddToFullList(boolean addToFullList) throws CMException {
        getWritableContentList().setAllowAddToFullList(addToFullList);
    }

    public void setEntry(int index, ContentReference contentRef)
            throws CMException {
        getWritableContentList().setEntry(index, contentRef);
    }

    public void setMaxSize(int maxSize) throws CMException {
        getWritableContentList().setMaxSize(maxSize);
    }

    public int getMaxSize() throws CMException {
        return getWritableContentList().getMaxSize();
    }
}

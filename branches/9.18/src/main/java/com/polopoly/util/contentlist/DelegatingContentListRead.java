package com.polopoly.util.contentlist;

import java.util.List;
import java.util.ListIterator;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentReference;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.collections.ContentListRead;

public class DelegatingContentListRead {
    private ContentListRead delegate;

    protected ContentListRead getDelegate() {
        return delegate;
    }

    public DelegatingContentListRead(ContentListRead delegate) {
        this.delegate = delegate;
    }

    public String getContentListStorageGroup() throws CMException {
        return delegate.getContentListStorageGroup();
    }

    public ContentReference getEntry(int index) throws CMException {
        return delegate.getEntry(index);
    }

    @SuppressWarnings("unchecked")
    public ListIterator<ContentReference> getListIterator() {
        return delegate.getListIterator();
    }

    @Deprecated
    public ContentId getReferredContentId(int index) throws CMException {
        return delegate.getReferredContentId(index);
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    public List<ContentId> getReferredContentIds() throws CMException {
        return delegate.getReferredContentIds();
    }

    @Deprecated
    public ContentId getTocEntryId(int index) throws CMException {
        return delegate.getTocEntryId(index);
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    public List<ContentId> getTocEntryIds() throws CMException {
        return delegate.getTocEntryIds();
    }

    @Deprecated
    public int indexOf(ContentId contentId) throws CMException {
        return delegate.indexOf(contentId);
    }

    public boolean isReadOnly() {
        return delegate.isReadOnly();
    }

    public int size() {
        return delegate.size();
    }

}

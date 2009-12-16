package com.polopoly.util.contentlist;

import java.util.Iterator;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.collections.ContentListRead;
import com.polopoly.util.client.PolopolyContext;

public class ContentReferenceIterator implements Iterator<ContentReferenceUtil> {
    private int i = 0;
    private int size;
    private ContentListRead contentList;
    private PolopolyContext context;

    public ContentReferenceIterator(ContentListRead contentList, PolopolyContext context) {
        size = contentList.size();
        this.contentList = contentList;
        this.context = context;
    }

    public boolean hasNext() {
        return i < size;
    }

    public ContentReferenceUtil next() {
        try {
            return new ContentReferenceUtil(contentList.getEntry(i++), contentList, context);
        } catch (CMException e) {
            throw new CMRuntimeException("While iterating through content list " + contentList + ": " + e.getMessage(), e);
        }
    }

    public void remove() {
    }

}

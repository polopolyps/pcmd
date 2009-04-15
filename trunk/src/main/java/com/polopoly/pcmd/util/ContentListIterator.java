package com.polopoly.pcmd.util;

import com.polopoly.cm.ContentReference;
import com.polopoly.cm.collections.ContentListRead;
import com.polopoly.cm.collections.iterator.InSequenceContentListIterator;

public class ContentListIterator extends InSequenceContentListIterator {

    public ContentListIterator(ContentListRead contentList) {
        super(contentList);
    }

    @Override
    public Object next() {
        return ((ContentReference) super.next()).getReferredContentId();
    }
}

package com.polopoly.util.contentlist;

import com.polopoly.cm.ContentReference;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.collections.ContentListRead;

public class RuntimeExceptionContentListWrapper extends DelegatingContentList implements RuntimeExceptionContentList {

    public RuntimeExceptionContentListWrapper(ContentListRead delegate) {
        super(delegate);
    }

    @Override
    public ContentReference getEntry(int index) {
        try {
            return super.getEntry(index);
        } catch (CMException e) {
            throw toRuntimeException(e, "getEntry");
        }
    }

    private RuntimeException toRuntimeException(Exception e, String operation) {
        return new CMRuntimeException("While performing operation " + operation + " on " +
                this + ": " + e.getMessage(), e);
    }
}

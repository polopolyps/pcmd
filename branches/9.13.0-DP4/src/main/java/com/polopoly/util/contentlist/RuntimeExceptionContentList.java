package com.polopoly.util.contentlist;

import com.polopoly.cm.ContentReference;
import com.polopoly.cm.collections.ContentList;

public interface RuntimeExceptionContentList extends ContentList {

    ContentReference getEntry(int index);
}

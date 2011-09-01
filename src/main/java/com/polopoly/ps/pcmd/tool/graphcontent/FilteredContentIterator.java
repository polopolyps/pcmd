/**
 * 
 */
package com.polopoly.ps.pcmd.tool.graphcontent;

import java.util.Iterator;
import com.polopoly.cm.ContentId;
import com.polopoly.ps.pcmd.tool.graphcontent.filter.ContentFilter;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.FetchingIterator;
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.exception.ContentGetException;

public class FilteredContentIterator extends FetchingIterator<ContentUtil> {
    private final PolopolyContext context;
    private final ContentFilter seedFilter;
    private final Iterator<ContentId> contentIds;
    ContentUtil next = null;

    public FilteredContentIterator(PolopolyContext context, ContentFilter seedFilter, Iterator<ContentId> contentIds) {
        this.context = context;
        this.seedFilter = seedFilter;
        this.contentIds = contentIds;
    }

    protected ContentUtil fetch() {
        while(contentIds.hasNext()) {
            try {
                ContentId id = contentIds.next();
                if (seedFilter.accepts(id)) {
                    ContentUtil content = context.getContent(id);
                    if (seedFilter.accepts(content)) {
                        return content;
                    }
                }
            } catch (ContentGetException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
}
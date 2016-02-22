package com.polopoly.ps.pcmd.tool.graphcontent.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.polopoly.cm.ContentId;
import com.polopoly.util.content.ContentUtil;

public class AggregateContentFilter implements ContentFilter {
    private List<ContentFilter> filters = new ArrayList<ContentFilter>();
    
    public AggregateContentFilter(ContentFilter... filters) {
        this.filters.addAll(Arrays.asList(filters));
    }
    
    public void add(ContentFilter filter) {
        filters.add(filter);
    }
    
    public boolean accepts(ContentUtil content) {
        for(ContentFilter filter : filters) {
            if (!filter.accepts(content)) {
                return false;
            }
        }
        
        return true;
    }

    public boolean accepts(ContentId id) {
        for(ContentFilter filter : filters) {
            if (!filter.accepts(id)) {
                return false;
            }
        }
        
        return true;
    }    
}
package com.polopoly.ps.pcmd.tool.graphcontent.filter;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.server.ServerNames;
import com.polopoly.util.content.ContentUtil;

public class PrefixContentFilter implements ContentFilter {
    private String prefix;
    private boolean exclude;
    
    public PrefixContentFilter(String prefix, boolean exclude) {
        this.prefix = prefix;
        this.exclude = exclude;
    }

    public boolean accepts(ContentUtil content) {
        String name = content.getContentIdString();
        if (name == null)
            name = content.getComponent(ServerNames.CONTENT_ATTRG_SYSTEM, ServerNames.CONTENT_ATTR_NAME);
        if (name == null)
            return exclude;
        else 
            return name.startsWith(prefix) ^ exclude;
    }

    public boolean accepts(ContentId id) {
        return true;
    }
}
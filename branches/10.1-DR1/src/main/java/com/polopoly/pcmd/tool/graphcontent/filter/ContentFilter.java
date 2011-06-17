package com.polopoly.pcmd.tool.graphcontent.filter;

import com.polopoly.cm.ContentId;
import com.polopoly.util.content.ContentUtil;

public interface ContentFilter {
    boolean accepts(ContentUtil content);
    boolean accepts(ContentId id);
}
package com.polopoly.pcmd.tool.graphcontent.model;

import java.util.Set;
import java.util.TreeSet;

import com.polopoly.cm.ContentId;

public class ContentNode {
    private ContentId contentId;
    private Set<String> tags = new TreeSet<String>();
    
    public ContentNode(ContentId contentId) {
        this.contentId = contentId;
    }
    
    public ContentId getContentId() {
        return contentId;
    }
    
    public void tag(String tag) {
        tags.add(tag);
    }
    
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }
    
    public Set<String> getTags() {
        return tags;
    }
}

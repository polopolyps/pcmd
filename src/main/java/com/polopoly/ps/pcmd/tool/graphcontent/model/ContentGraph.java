package com.polopoly.ps.pcmd.tool.graphcontent.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.polopoly.cm.ContentId;
import com.polopoly.util.content.ContentUtil;

public class ContentGraph {
    private References references;
    private Map<ContentId, ContentNode> content = new HashMap<ContentId, ContentNode>();
    
    public ContentGraph(boolean removeDuplicateReferences) {
        references = new References(removeDuplicateReferences);
    }
    
    public References getReferences() {
        return references;
    }
    
    public Reference addReference(ContentId fromId, ContentId toId) {
        return addReference(fromId, toId, null);
    }
    
    public Reference addReference(ContentId fromId, ContentId toId, ContentId rmdId) {
        return references.add(fromId.getContentId(), toId.getContentId(), rmdId == null ? null : rmdId.getContentId());
    }
    
    public ContentNode addNode(ContentId contentId) {
        ContentId unversionedId = contentId.getContentId();
        ContentNode node = new ContentNode(unversionedId);
        content.put(unversionedId, node);
        return node;
    }
    
    public ContentNode addNodeAndAutoTag(ContentUtil content) {
        ContentNode node = addNode(content.getContentId());
        node.tag("inputtemplate:" + content.getInputTemplate().getExternalIdString());
        if (content.getLockInfo() != null) node.tag("locked");
        node.tag("major:" + content.getContentId().getMajor());
        return node;
    }
    
    public boolean hasNodeFor(ContentId contentId) {
        return content.containsKey(contentId.getContentId());
    }
    
    public ContentNode getNodeFor(ContentId contentId) {
        return content.get(contentId.getContentId());
    }
    
    public Collection<ContentNode> getContentNodes() {
        return content.values();
    }
    
    public Collection<ContentId> getContentIds() {
        return content.keySet();
    }
}

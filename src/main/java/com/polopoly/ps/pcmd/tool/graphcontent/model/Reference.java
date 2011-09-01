package com.polopoly.ps.pcmd.tool.graphcontent.model;

import java.util.Set;
import java.util.TreeSet;

import com.polopoly.cm.ContentId;

public class Reference {
    private ContentId fromId;
    private ContentId toId;
    private ContentId rmdId;
    private Set<String> tags = new TreeSet<String>();

    public Reference(ContentId fromId, ContentId toId) {
        this(fromId, toId, null);
    }
    
    public Reference(ContentId fromId, ContentId toId, ContentId rmdId) {
        this.fromId = fromId;
        this.toId = toId;
        this.rmdId = rmdId;
    }

    
    public ContentId getFromId() {
        return fromId;
    }
    
    public ContentId getToId() {
        return toId;
    }
    
    public ContentId getRmdId() {
        return rmdId;
    }
    
    public void setRmdId(ContentId rmdId) {
        this.rmdId = rmdId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Reference)) {
            return false;
        }
        
        Reference other = (Reference) obj;
        if (other.fromId.equals(this.fromId) && other.toId.equals(this.toId)) {
            if (other.rmdId == null && this.rmdId == null) {
                return true;
            } else if(other.rmdId != null && this.rmdId != null) {
                return other.rmdId.equals(this.rmdId); 
            }
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return fromId.hashCode() - toId.hashCode() - rmdId.hashCode();
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

package com.polopoly.ps.pcmd.tool.graphcontent.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.polopoly.cm.ContentId;

public class References {
    private boolean removeDuplicates;
    private Map<ContentId, List<Reference>> forward = new HashMap<ContentId, List<Reference>>();
    private Map<ContentId, List<Reference>> backward = new HashMap<ContentId, List<Reference>>();
    
    public References(boolean removeDuplicates) {
        this.removeDuplicates = removeDuplicates;
    }
    
    public Reference add(ContentId fromId, ContentId toId) {
        return add(fromId, toId, null);
    }
    
    public Reference add(ContentId fromId, ContentId toId, ContentId rmdId) {
        Reference ref = new Reference(fromId, toId, rmdId); 
        add(ref);
        return ref;
    }
    
    public void add(Reference ref) {
        if (!removeDuplicates || !exists(ref)) {
            getReferencesFrom(ref.getFromId()).add(ref);
            getReferencesTo(ref.getToId()).add(ref);
        }
    }
    
    public List<Reference> getReferencesFrom(ContentId contentId) {
        return ensureMapList(forward, contentId);
    }
    
    public List<Reference> getReferencesTo(ContentId contentId) {
        return ensureMapList(backward, contentId);
    }
    
    public boolean exists(Reference ref) {
        List<Reference> forwardRefs = getReferencesFrom(ref.getFromId());
        return forwardRefs.contains(ref);
    }
    
    public List<Reference> getReferenceList() {
        List<Reference> allRefs = new ArrayList<Reference>();
        for (ContentId key : forward.keySet()) {
            allRefs.addAll(forward.get(key));
        }
        
        return allRefs;
    }
    
    private static <T, U> List<U> ensureMapList(Map<T, List<U>> map, T id) {
        if (!map.containsKey(id)) {
            map.put(id, new ArrayList<U>());
        }
        
        return map.get(id);
    }
}

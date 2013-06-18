package com.polopoly.ps.pcmd.xml.allcontent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.polopoly.ps.pcmd.client.Major;

public class AllContent {
    private Map<Major, Set<String>> externalIdsByMajor = new HashMap<Major, Set<String>>();

    public void add(Major major, String externalId) {
        getExternalIds(major).add(externalId);
    }

    public Set<Major> getMajors() {
        return externalIdsByMajor.keySet();
    }

    public Set<String> getExternalIds(Major major) {
        Set<String> result = externalIdsByMajor.get(major);

        if (result == null) {
            result = new HashSet<String>();
            externalIdsByMajor.put(major, result);
        }

        return result;
    }

    public List<String> getAllExternalIds() {
        List<String> externalIds = new ArrayList<String>();
        List<Set<String>> listOfSet = new ArrayList<Set<String>>(externalIdsByMajor.values());
        for (Set<String> set : listOfSet) {
            for (String id : set) {
                externalIds.add(id);
            }
        }
        return externalIds;
    }
}

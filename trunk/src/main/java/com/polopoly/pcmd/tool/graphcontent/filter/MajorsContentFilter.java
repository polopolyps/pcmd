package com.polopoly.pcmd.tool.graphcontent.filter;

import java.util.Arrays;
import java.util.List;

import com.polopoly.cm.ContentId;
import com.polopoly.util.content.ContentUtil;

public class MajorsContentFilter implements ContentFilter {
    protected List<Integer> majors;

    public MajorsContentFilter(List<Integer> majors) {
        this.majors = majors;
    }

    public MajorsContentFilter(Integer... majors) {
        this.majors = Arrays.asList(majors);
    }
    
    public boolean accepts(ContentUtil content) {
        return majors.contains(content.getContentId().getMajor());
    }

    public boolean accepts(ContentId id) {
        return majors.contains(id.getMajor());
    }
}

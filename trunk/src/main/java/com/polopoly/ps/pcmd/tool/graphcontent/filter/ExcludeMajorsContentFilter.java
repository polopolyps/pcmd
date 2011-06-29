package com.polopoly.ps.pcmd.tool.graphcontent.filter;

import java.util.Arrays;
import java.util.List;

import com.polopoly.cm.ContentId;
import com.polopoly.util.content.ContentUtil;

public class ExcludeMajorsContentFilter implements ContentFilter {
    protected List<Integer> majors;

    public ExcludeMajorsContentFilter(List<Integer> majors) {
        this.majors = majors;
    }

    public ExcludeMajorsContentFilter(Integer... majors) {
        this.majors = Arrays.asList(majors);
    }
    
    public boolean accepts(ContentUtil content) {
        return !majors.contains(content.getContentId().getMajor());
    }

    public boolean accepts(ContentId id) {
        return !majors.contains(id.getMajor());
    }
}

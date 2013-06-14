package com.polopoly.ps.pcmd.xml.parser;

import com.polopoly.ps.pcmd.client.Major;
import com.polopoly.ps.pcmd.util.SingleObjectHolder;
import com.polopoly.ps.pcmd.util.Tuple;

public class ParsedContentId extends SingleObjectHolder<Tuple<Major, String>> {
    private Major major;
    private String externalId;

    public ParsedContentId(Major major, String externalId) {
        super(new Tuple<Major, String>(major, externalId));
        this.major = (major == null ? Major.UNKNOWN : major);
        this.externalId = externalId;
    }

    public Major getMajor() {
        return major;
    }

    public String getExternalId() {
        return externalId;
    }

}

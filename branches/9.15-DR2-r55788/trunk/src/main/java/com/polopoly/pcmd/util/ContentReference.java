package com.polopoly.pcmd.util;

public class ContentReference {
    private String group;
    private String reference;

    public ContentReference(String group, String reference) {
        this.group = group;
        this.reference = reference;
    }

    public String getGroup() {
        return group;
    }

    public String getReference() {
        return reference;
    }
}

package com.polopoly.ps.pcmd.text;

public class Publishing {
    private Reference publishIn;
    private String publishInGroup;

    public Publishing(Reference publishIn, String publishInGroup) {
        super();
        this.publishIn = publishIn;
        this.publishInGroup = publishInGroup;
    }

    public Reference getPublishIn() {
        return publishIn;
    }

    public String getPublishInGroup() {
        return publishInGroup;
    }
}

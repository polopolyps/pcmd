package com.polopoly.ps.pcmd.field.content;

import com.polopoly.cm.client.ContentRead;
import com.polopoly.util.client.PolopolyContext;

public class VersionField implements Field {

    public String get(ContentRead content, PolopolyContext context) {
        return Integer.toString(content.getContentId().getVersion());
    }

}

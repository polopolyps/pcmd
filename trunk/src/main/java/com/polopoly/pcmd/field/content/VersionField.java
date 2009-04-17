package com.polopoly.pcmd.field.content;

import com.polopoly.cm.client.ContentRead;
import com.polopoly.pcmd.tool.PolopolyContext;

public class VersionField implements Field {

    public String get(ContentRead content, PolopolyContext context) {
        return Integer.toString(content.getContentId().getVersion());
    }

}

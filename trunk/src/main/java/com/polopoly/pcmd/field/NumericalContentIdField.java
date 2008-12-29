package com.polopoly.pcmd.field;

import com.polopoly.cm.client.ContentRead;
import com.polopoly.pcmd.tool.PolopolyContext;

public class NumericalContentIdField implements Field {

    public String get(ContentRead content, PolopolyContext context) {
        return content.getContentId().getContentIdString();
    }

}

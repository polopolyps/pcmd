package com.polopoly.ps.pcmd.field.content;

import com.polopoly.cm.client.ContentRead;
import com.polopoly.util.client.PolopolyContext;

public class NumericalContentIdField implements Field {

    public String get(ContentRead content, PolopolyContext context) {
        return content.getContentId().getContentIdString();
    }

}

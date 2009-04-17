package com.polopoly.pcmd.field.content;

import com.polopoly.cm.client.ContentRead;
import com.polopoly.pcmd.tool.PolopolyContext;

public interface Field {
    String get(ContentRead content, PolopolyContext context);
}

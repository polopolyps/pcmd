package com.polopoly.pcmd.field.content;

import com.polopoly.cm.client.ContentRead;
import com.polopoly.pcmd.tool.PolopolyContext;

public class PaddingField implements Field {
    private Field field;
    private int size;

    public PaddingField(Field field, int size) {
        this.field = field;
        this.size = size;
    }

    public String get(ContentRead content, PolopolyContext context) {
        StringBuffer result =
            new StringBuffer(field.get(content, context));

        if (result.length() > size) {
            result.setLength(size - 2);
            result.append("..");
        }

        while (result.length() < size) {
            result.append(' ');
        }

        return result.toString();
    }

}

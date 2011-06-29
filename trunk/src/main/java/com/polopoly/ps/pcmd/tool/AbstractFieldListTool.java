package com.polopoly.ps.pcmd.tool;

import com.polopoly.cm.client.ContentRead;
import com.polopoly.ps.pcmd.field.content.Field;
import com.polopoly.util.client.PolopolyContext;

public abstract class AbstractFieldListTool<T extends FieldListParameters> implements Tool<T> {
    protected StringBuffer getFieldValues(PolopolyContext context,
            ContentRead content, FieldListParameters parameters) {
        StringBuffer line = new StringBuffer(100);
        line.setLength(0);

        boolean first = true;

        for (Field field : parameters.getFieldList()) {
            if (!first) {
                line.append(parameters.getDelimiter());
            }
            else {
                first = false;
            }

            line.append(field.get(content, context));
        }

        return line;
    }
}

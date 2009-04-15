package com.polopoly.pcmd.tool;

import com.polopoly.cm.client.ContentRead;
import com.polopoly.pcmd.field.Field;
import com.polopoly.pcmd.util.ContentIdToContentIterator;

public class ListTool implements Tool<ListParameters> {
    public ListParameters createParameters() {
        return new ListParameters();
    }

    public void execute(PolopolyContext context, ListParameters parameters) {
        ContentIdToContentIterator it =
            new ContentIdToContentIterator(context, parameters.getContentIds(), parameters.isStopOnException());

        StringBuffer line = new StringBuffer(100);

        while (it.hasNext()) {
            line.setLength(0);

            ContentRead content = it.next();

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

            System.out.println(line);
        }

        it.printInfo(System.err);
    }

    public String getHelp() {
        return "Lists the specified information on the specified content IDs";
    }
}



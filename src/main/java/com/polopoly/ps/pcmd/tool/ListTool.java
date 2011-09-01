package com.polopoly.ps.pcmd.tool;

import com.polopoly.cm.client.ContentRead;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.ContentIdToContentIterator;

public class ListTool extends AbstractFieldListTool<ListParameters> {
    public ListParameters createParameters() {
        return new ListParameters();
    }

    public void execute(PolopolyContext context, ListParameters parameters) {
        ContentIdToContentIterator it =
            new ContentIdToContentIterator(context, parameters.getContentIds(), parameters.isStopOnException());

        while (it.hasNext()) {
            ContentRead content = it.next();

            System.out.println(getFieldValues(context, content, parameters));
        }

        it.printInfo(System.err);
    }

    public String getHelp() {
        return "Lists the specified information on the specified content IDs";
    }
}



package com.polopoly.ps.pcmd.tool;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.Content;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.ps.pcmd.argument.ContentIdListParameters;
import com.polopoly.ps.pcmd.field.content.AbstractContentIdField;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.ContentIdToContentIterator;

public class LockTool implements Tool<ContentIdListParameters> {
    public ContentIdListParameters createParameters() {
        return new ContentIdListParameters();
    }

    public void execute(PolopolyContext context,
            ContentIdListParameters parameters) {
        ContentIdToContentIterator it =
            new ContentIdToContentIterator(context, parameters.getContentIds(), parameters.isStopOnException());

        while (it.hasNext()) {
            ContentRead content = it.next();

            try {
                ((Content) content).lock();

                System.out.println(AbstractContentIdField.get(content.getContentId(), context));
            } catch (CMException e) {
                if (parameters.isStopOnException()) {
                    throw new CMRuntimeException(e);
                }
                else {
                    System.err.println(content.getContentId().getContentIdString() + ": " + e);
                }
            }
        }

        it.printInfo(System.err);
    }

    public String getHelp() {
        return "Locks the specified content objects.";
    }
}

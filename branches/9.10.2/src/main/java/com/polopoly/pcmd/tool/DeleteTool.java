package com.polopoly.pcmd.tool;

import java.util.Iterator;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.pcmd.argument.ContentIdListParameters;

public class DeleteTool implements Tool<ContentIdListParameters> {
    public ContentIdListParameters createParameters() {
        return new ContentIdListParameters();
    }

    public void execute(PolopolyContext context,
            ContentIdListParameters parameters) {
        Iterator<ContentId> it = parameters.getContentIds();

        while (it.hasNext()) {
            ContentId contentId = it.next();

            try {
                context.getPolicyCMServer().removeContent(contentId);
            } catch (CMException e) {
                String errorString = "While removing " + contentId.getContentIdString() + ": " + e;

                if (parameters.isStopOnException()) {
                    throw new CMRuntimeException(errorString, e);
                }
                else {
                    System.err.println(errorString);
                }
            }
        }
    }

    public String getHelp() {
        return "Deletes the specified content objects.";
    }
}

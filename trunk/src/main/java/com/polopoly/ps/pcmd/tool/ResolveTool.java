package com.polopoly.ps.pcmd.tool;

import java.util.Iterator;

import com.polopoly.cm.ContentId;
import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.argument.ContentIdListParameters;
import com.polopoly.util.client.PolopolyContext;

public class ResolveTool implements Tool<ContentIdListParameters> {

    public ContentIdListParameters createParameters() {
        return new ContentIdListParameters();
    }

    public void execute(PolopolyContext context,
            ContentIdListParameters parameters) {
        Iterator<ContentId> it = parameters.getContentIds();

        while (it.hasNext()) {
            System.out.println(it.next().getContentIdString());
        }
    }

    public String getHelp() {
        return "Resolves the content IDs into the default stage versioned content IDs (IDs may be external IDs)";
    }
}


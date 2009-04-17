package com.polopoly.pcmd.field.content;

import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.client.WorkflowAware;
import com.polopoly.pcmd.tool.PolopolyContext;

public class WorkflowField implements Field {

    public String get(ContentRead content, PolopolyContext context) {
        if (content instanceof WorkflowAware) {
            VersionedContentId workflowId;
            try {
                workflowId = ((WorkflowAware) content).getWorkflowId();
            } catch (CMException e) {
                System.err.println(e.toString());

                workflowId = null;
            }

            String state = "";

            if (workflowId != null) {
                try {
                    state = ((WorkflowAware) content).getWorkflowState().getName();
                } catch (CMException e) {
                    System.err.println(e.toString());
                }
            }

            return AbstractContentIdField.get(workflowId, context) + " " + state;
        }
        else {
            return "-";
        }
    }

}

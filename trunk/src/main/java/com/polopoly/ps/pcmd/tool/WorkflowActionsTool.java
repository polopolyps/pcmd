package com.polopoly.ps.pcmd.tool;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.client.WorkflowAware;
import com.polopoly.cm.workflow.WorkflowAction;
import com.polopoly.ps.pcmd.field.content.AbstractContentIdField;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.ContentIdToContentIterator;

public class WorkflowActionsTool implements Tool<WorkflowActionsParameters> {

    public WorkflowActionsParameters createParameters() {
        return new WorkflowActionsParameters();
    }

    public void execute(PolopolyContext context,
            WorkflowActionsParameters parameters) {
        ContentIdToContentIterator it =
            new ContentIdToContentIterator(context, parameters.getContentIds(), parameters.isStopOnException());

        while (it.hasNext()) {
            ContentRead content = it.next();

            System.out.print(AbstractContentIdField.get(content.getContentId().getContentId(), context) + ":");

            if (content instanceof WorkflowAware) {
                WorkflowAware wa = (WorkflowAware) content;

                WorkflowAction[] actions;

                try {
                    actions = wa.getWorkflowActions();
                } catch (CMException e) {
                    System.err.println("While getting actions: " + e.toString());

                    if (it.isStopOnException()) {
                        break;
                    }
                    else {
                        continue;
                    }
                }

                String performAction = parameters.getPerform();

                if (performAction != null) {
                    try {
                        boolean found = false;

                        for (WorkflowAction action : actions) {
                            if (performAction.equals(action.getName())) {
                                wa.doWorkflowAction(action);
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            System.err.println("The action " + performAction + " was not available.");
                        }

                        try {
                            actions = wa.getWorkflowActions();
                        } catch (CMException e) {
                            System.err.println("While getting actions: " + e.toString());

                            if (it.isStopOnException()) {
                                break;
                            }
                            else {
                                continue;
                            }
                        }
                    } catch (CMException e) {
                        System.err.println("While performing action: " + e.toString());
                    }
                }

                boolean first = true;

                for (WorkflowAction action : actions) {
                    if (first) {
                        first = false;
                    }
                    else {
                        System.out.print(", ");
                    }

                    System.out.print(action.getName());
                }
            }
            else {
                System.err.println("Not workflow aware.");
            }

            System.out.println();
        }

        it.printInfo(System.err);
    }

    public String getHelp() {
        return "Inspect available workflow actions or perform a workflow action.";
    }

}

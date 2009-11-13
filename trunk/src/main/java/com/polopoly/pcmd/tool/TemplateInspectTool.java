package com.polopoly.pcmd.tool;

import com.polopoly.cm.server.ServerNames;
import com.polopoly.pcmd.argument.ContentIdListParameters;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.ContentIdToContentIterator;
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.contentid.ContentIdUtil;
import com.polopoly.util.contentlist.ContentReferenceUtil;
import com.polopoly.util.exception.ContentGetException;
import com.polopoly.util.policy.Util;

public class TemplateInspectTool implements Tool<ContentIdListParameters> {
    private static final int TAB_SIZE = 2;

    private static final String OT_DATA = "otData";
    private static final String WIDGET_CLASS_NAME = "widgetClassName";
    private static final String ORCHID = "orchid";

    public ContentIdListParameters createParameters() {
        return new ContentIdListParameters();
    }

    public void execute(PolopolyContext context,
            ContentIdListParameters parameters) {
        ContentIdToContentIterator it =
            new ContentIdToContentIterator(context, parameters.getContentIds(), parameters.isStopOnException());

        while (it.hasNext()) {
            ContentUtil content = Util.util(it.next(), context);

            printTemplate(context, content, 0);
        }

        it.printInfo(System.err);

    }

    private void printTemplate(PolopolyContext context, ContentUtil templateContent, int level) {
        String indent = getIndent(level * TAB_SIZE);

        printIndented(indent, "Template: " + templateContent.getExternalIdString());

        printIndented(indent, "Policy class: " + templateContent.getComponent(ServerNames.IT_ATTRG_SYSTEM, ServerNames.IT_ATTR_POLICY));

        try {
            ContentIdUtil viewer = templateContent.getContentReference(
                    ServerNames.IT_ATTRG_SYSTEM, ServerNames.IT_ATTR_VIEWER + ORCHID);

            if (viewer != null) {
                printIndented(indent, "Viewer: " + viewer.asContent().
                        getComponent(OT_DATA, WIDGET_CLASS_NAME));
            }
        } catch (ContentGetException e) {
        }

        try {
            ContentIdUtil editor = templateContent.getContentReference(
                    ServerNames.IT_ATTRG_SYSTEM, ServerNames.IT_ATTR_EDITOR + ORCHID);

            if (editor != null) {
                printIndented(indent, "Editor: " + editor.asContent().
                        getComponent(OT_DATA, WIDGET_CLASS_NAME));
            }
        } catch (ContentGetException e) {
        }

        for (ContentReferenceUtil fieldReference : templateContent.getContentList(ServerNames.IT_ATTRG_SUBTEMPLATES).references()) {
            try {
                printIndented(indent, "- " + fieldReference.getReferenceMetaDataId().asContent().getName());
                printTemplate(context, fieldReference.getReferredContentId().asContent(), level+1);
            } catch (ContentGetException e) {
            }
        }
    }

    private void printIndented(String indent, String string) {
        System.out.print(indent);
        System.out.println(string);
    }

    private String getIndent(int i) {
        StringBuffer result = new StringBuffer(i);

        for (int j = 0; j < i; j++) {
            result.append(' ');
        }

        return result.toString();
    }


    public String getHelp() {
        return "Prints the fields, policy, viewer and editor of the specified template.";
    }

}

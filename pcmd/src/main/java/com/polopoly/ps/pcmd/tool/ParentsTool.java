package com.polopoly.ps.pcmd.tool;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.server.ServerNames;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.ContentIdToContentIterator;
import com.polopoly.util.exception.ContentGetException;

public class ParentsTool extends AbstractFieldListTool<FieldListAndContentIdListParameters> {
    private static final int TAB_SIZE = 2;
    private PolopolyContext context;

    private enum ParentType {
        SECURITY ("security parent"),
        INSERT ("insert parent"),
        UNSPECIFIED ("");

        private String name;

        ParentType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public FieldListAndContentIdListParameters createParameters() {
        return new FieldListAndContentIdListParameters();
    }

    public void execute(PolopolyContext context,
            FieldListAndContentIdListParameters parameters) {
        this.context = context;

        ContentIdToContentIterator it =
            new ContentIdToContentIterator(context, parameters.getContentIds(), parameters.isStopOnException());

        StringBuffer line = new StringBuffer(100);

        while (it.hasNext()) {
            line.setLength(0);

            ContentRead content = it.next();

            printLevel(content, 0, ParentType.UNSPECIFIED, parameters);

            System.out.println(line);
        }

        it.printInfo(System.err);
    }

    private static boolean equals(ContentId aContentId, ContentId anotherContentId) {
        if (aContentId == null) {
            return anotherContentId == null;
        }
        else {
            return aContentId.equalsIgnoreVersion(anotherContentId);
        }
    }

    private void printIndent(int level) {
        int tab = level * TAB_SIZE;

        for (int j = 0; j < tab; j++) {
            System.out.print(' ');
        }
    }

    private void printLevel(ContentId contentId,
            int level, ParentType parentType, FieldListAndContentIdListParameters parameters) {
        try {
            if (contentId != null) {
                printLevel(context.getContent(contentId), level, parentType, parameters);
            }
        } catch (ContentGetException e) {
            System.err.println(e.getMessage());
        }
    }

    private void printLevel(ContentRead content, int level, ParentType parentType, FieldListAndContentIdListParameters parameters) {
        printIndent(level);

        StringBuffer line = getFieldValues(context, content, parameters);

        if (parentType != ParentType.UNSPECIFIED) {
            line.append(" (" + parentType + ")");
        }

        System.out.println(line);

        ContentId insertParent = null;

        try {
            insertParent = content.getContentReference(
                    ServerNames.CONTENT_ATTRG_PARENT,
                    ServerNames.CONTENT_ATTR_INSERT_PARENTID);
        } catch (CMException e) {
            System.err.println(e.toString());
        }

        ContentId securityParent = content.getSecurityParentId();

        boolean bothParentsEqual = equals(insertParent, securityParent);

        if (!bothParentsEqual) {
            printLevel(securityParent, level+1, ParentType.SECURITY, parameters);
            printLevel(insertParent, level+1, ParentType.INSERT, parameters);
        }
        else {
            printLevel(insertParent, level+1, ParentType.UNSPECIFIED, parameters);
        }
    }
    public String getHelp() {
        return "Prints the security parent and insert parent chain of the specified objects.";
    }

}

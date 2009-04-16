package com.polopoly.pcmd.tool;

import java.util.Iterator;
import java.util.List;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.pcmd.field.Field;
import com.polopoly.pcmd.util.ContentListIterator;

public class HierarchyTool implements Tool<HierarchyParameters>{
    private static final int TAB_SIZE = 2;
    private List<Field> fieldList;
    private String delimiter;

    public HierarchyParameters createParameters() {
        return new HierarchyParameters();
    }

    public void execute(PolopolyContext context, HierarchyParameters parameters) {
        fieldList = parameters.getFieldList();
        delimiter = parameters.getDelimiter();

        printLevel(0, parameters.getRoot(), context);
    }

    private StringBuffer line = new StringBuffer(100);

    @SuppressWarnings("unchecked")
    private void printLevel(int level, ContentId root, PolopolyContext context) {
        line.setLength(0);

        try {
            ContentRead content = context.getPolicyCMServer().getContent(root);

            int tab = level * TAB_SIZE;

            for (int j = 0; j < tab; j++) {
                System.out.print(' ');
            }

            boolean first = true;

            for (Field field : fieldList) {
                if (!first) {
                    line.append(delimiter);
                }
                else {
                    first = false;
                }

                line.append(field.get(content, context));
            }

            System.out.println(line);

            Iterator<ContentId> it =
                new ContentListIterator(content.getContentList());

            while (it.hasNext()) {
                printLevel(level+1, it.next().getContentId(), context);
            }
        } catch (CMException e) {
            System.err.print(e.toString());
        }
    }

    public String getHelp() {
        return "Prints the content hierarchy (equivalent to the left-hand side navigation in the GUI).";
    }
}

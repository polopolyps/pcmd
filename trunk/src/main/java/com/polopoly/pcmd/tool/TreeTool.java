package com.polopoly.pcmd.tool;

import java.util.List;

import com.polopoly.cm.client.ContentRead;
import com.polopoly.pcmd.field.content.Field;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.exception.ContentGetException;
import com.polopoly.util.policy.ContentListUtil;
import static com.polopoly.util.policy.Util.*;

public class TreeTool implements Tool<TreeParameters> {
    private static final int TAB_SIZE = 2;
    private List<Field> fieldList;
    private String delimiter;

    public TreeParameters createParameters() {
        return new TreeParameters();
    }

    public void execute(PolopolyContext context, TreeParameters parameters) {
        fieldList = parameters.getFieldList();
        delimiter = parameters.getDelimiter();

        try {
            printLevel(0, parameters.getDepth(), context.getContent(parameters.getRoot()), context);
        } catch (ContentGetException e) {
            System.err.print(e.toString());
        }
    }

    private StringBuffer line = new StringBuffer(100);

    private void printLevel(int level, int depth, ContentRead root, PolopolyContext context) {
        line.setLength(0);

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

            line.append(field.get(root, context));
        }

        System.out.println(line);

        if (level < depth-1) {
            ContentListUtil contentListUtil =
                util(root, context).getContentList();

            for (ContentRead child : contentListUtil.getContents()) {
                printLevel(level+1, depth, child, context);
            }
        }
    }

    public String getHelp() {
        return "Prints the content hierarchy (equivalent to the left-hand side navigation in the GUI).";
    }
}

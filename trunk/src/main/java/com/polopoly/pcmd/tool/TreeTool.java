package com.polopoly.pcmd.tool;

import static com.polopoly.util.policy.Util.util;

import com.polopoly.cm.client.ContentRead;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.exception.ContentGetException;
import com.polopoly.util.policy.ContentListUtil;

public class TreeTool extends AbstractFieldListTool<TreeParameters>  {
    private static final int TAB_SIZE = 2;
    private int depth;
    private PolopolyContext context;

    public TreeParameters createParameters() {
        return new TreeParameters();
    }

    public void execute(PolopolyContext context, TreeParameters parameters) {
        this.depth = parameters.getDepth();
        this.context = context;

        try {
            printLevel(0, context.getContent(parameters.getRoot()), parameters);
        } catch (ContentGetException e) {
            System.err.print(e.toString());
        }
    }

    private StringBuffer line = new StringBuffer(100);

    private void printLevel(int level, ContentRead contentAtLevel, TreeParameters parameters) {
        line.setLength(0);

        printIndent(level);

        System.out.println(getFieldValues(context, contentAtLevel, parameters));

        if (level < depth-1) {
            ContentListUtil contentListUtil =
                util(contentAtLevel, context).getContentList();

            for (ContentRead child : contentListUtil.getContents()) {
                printLevel(level+1, child, parameters);
            }
        }
    }

    private void printIndent(int level) {
        int tab = level * TAB_SIZE;

        for (int j = 0; j < tab; j++) {
            System.out.print(' ');
        }
    }

    public String getHelp() {
        return "Prints the content hierarchy (equivalent to the left-hand side navigation in the GUI).";
    }
}

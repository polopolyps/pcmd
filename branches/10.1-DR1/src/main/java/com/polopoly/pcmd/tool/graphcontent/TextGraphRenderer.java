package com.polopoly.pcmd.tool.graphcontent;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.polopoly.cm.ContentId;
import com.polopoly.pcmd.field.content.AbstractContentIdField;
import com.polopoly.pcmd.tool.graphcontent.model.ContentGraph;
import com.polopoly.pcmd.tool.graphcontent.model.ContentNode;
import com.polopoly.pcmd.tool.graphcontent.model.Reference;
import com.polopoly.pcmd.util.dot.NodeStyle;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.exception.ContentGetException;

public class TextGraphRenderer implements GraphRenderer {

    private PolopolyContext context;

    public TextGraphRenderer(PolopolyContext context) {
        this.context = context;
    }

    public void render(ContentGraph graph, PrintStream output) throws Exception {
        List<ContentNode> startNodes = getStartNodes(graph);
        
        for(ContentNode node : startNodes) {
            output.println();
            renderForwardNode(graph, output, node.getContentId(), null, new ArrayList<ContentId>(), 0);
            output.println();
            renderBackwardNode(graph, output, node.getContentId(), null, new ArrayList<ContentId>(), 0);
        }
    }


    private void renderForwardNode(ContentGraph graph, PrintStream output, ContentId contentId, ContentId rmdId, List<ContentId> used, int level) {
        if (level == 0) {
            output.println(printLabel(contentId, rmdId) + " (FORWARD)");
        } else {
            for (int i = 0; i < level; i++) {
                output.print("   ");
            }
            output.println(printLabel(contentId, rmdId));
        }
        
        
        used.add(0, contentId);
        for(Reference ref : graph.getReferences().getReferencesFrom(contentId)) {
            ContentId refId = ref.getToId();
            if (!used.contains(refId)) {
                renderForwardNode(graph, output, refId, ref.getRmdId(), used, level + 1);
            }
        }
        used.remove(0);
    }

    private void renderBackwardNode(ContentGraph graph, PrintStream output, ContentId contentId, ContentId rmdId, List<ContentId> used, int level) {
        if (level == 0) {
            output.println(printLabel(contentId, rmdId) + " (BACKWARD)");
        } else {
            for (int i = 0; i < level; i++) {
                output.print("   ");
            }
            output.println(printLabel(contentId, rmdId));
        }

        used.add(0, contentId);
        for(Reference ref : graph.getReferences().getReferencesTo(contentId)) {
            ContentId refId = ref.getFromId();
            if (!used.contains(refId)) {
                renderBackwardNode(graph, output, refId, ref.getRmdId(), used, level + 1);
            }
        }
        used.remove(0);
    }

    private String printLabel(ContentId contentId, ContentId rmdId) {
        String abstractId;
        String name = null;
        try {
             abstractId = AbstractContentIdField.get(contentId, context);
             name = context.getContent(contentId).getName();
        } catch(ContentGetException e) {
            e.printStackTrace();
            abstractId = "(?)";
        }
        
        String label = abstractId;
        if (name != null && name.length() > 0 && !name.equals(abstractId))
            label = label + " [" + name + "]";

        String rmd = "";
        if (rmdId != null) {
            rmd = " (by " + printLabel(rmdId, null) + ")";
        }
        
        return label + rmd;
    }
    
    private List<ContentNode> getStartNodes(ContentGraph graph) {
        List<ContentNode> startNodes = new ArrayList<ContentNode>();
        for(ContentNode contentNode : graph.getContentNodes()) {
            if (contentNode.hasTag("origin")) {
                startNodes.add(contentNode);
            }
        }
        return startNodes;
    }

}

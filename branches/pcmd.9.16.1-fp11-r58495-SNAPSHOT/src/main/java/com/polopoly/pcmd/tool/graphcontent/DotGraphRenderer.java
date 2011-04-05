package com.polopoly.pcmd.tool.graphcontent;

import java.io.PrintStream;

import com.polopoly.cm.ContentId;
import com.polopoly.pcmd.field.content.AbstractContentIdField;
import com.polopoly.pcmd.tool.graphcontent.model.ContentGraph;
import com.polopoly.pcmd.tool.graphcontent.model.ContentNode;
import com.polopoly.pcmd.tool.graphcontent.model.Reference;
import com.polopoly.pcmd.util.dot.DotWriter;
import com.polopoly.pcmd.util.dot.EdgeStyle;
import com.polopoly.pcmd.util.dot.NodeStyle;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.exception.ContentGetException;

public class DotGraphRenderer implements GraphRenderer {
    private PolopolyContext context;
    private DotGraphStyler styler;

    public DotGraphRenderer(PolopolyContext context, DotGraphStyler styler) {
        this.context = context;
        this.styler = styler;
    }
    
    public void render(ContentGraph graph, PrintStream output) throws Exception {
        DotWriter writer = new DotWriter(output, styler.style(graph));
        writer.begin();        
        for(ContentNode contentNode : graph.getContentNodes()) {
            renderNode(writer, contentNode);
        }
        for(Reference ref : graph.getReferences().getReferenceList()) {
            renderEdge(writer, ref);
        }
        writer.end();
    }

    private void renderNode(DotWriter writer, ContentNode contentNode) throws Exception {
        ContentId contentId = contentNode.getContentId();
        String abstractId;
        String name = null;
        try {
             abstractId = AbstractContentIdField.get(contentId, context);
             name = context.getContent(contentId).getName();
        } catch(ContentGetException e) {
            e.printStackTrace();
            abstractId = "(?)";
        }
        
        NodeStyle style = styler.style(contentNode);
        String label = abstractId;
        if (name != null && name.length() > 0 && !name.equals(abstractId))
            label = label + "\n[" + name + "]";
        style = style.label(label);
        
        writer.printNode(abstractId, style);
    }

    private void renderEdge(DotWriter writer, Reference ref) {
        String abstractFromId = AbstractContentIdField.get(ref.getFromId(), context);
        String abstractRefId = AbstractContentIdField.get(ref.getToId(), context);

        EdgeStyle style = styler.style(ref);

        ContentId rmdId = ref.getRmdId();
        if (rmdId != null) {
            String abstractRmdId;
            String name = null;
            try {
                abstractRmdId = AbstractContentIdField.get(rmdId, context);
                name = context.getContent(rmdId).getName();
           } catch(ContentGetException e) {
               e.printStackTrace();
               abstractRmdId = "(?)";
           }
           
           String label = abstractRmdId;
           if (name != null && name.length() > 0 && !name.equals(abstractRmdId))
               label = label + "\n[" + name + "]";
           style = style.label(label);
        }
        
        writer.printEdge(abstractFromId, abstractRefId, style);    
    }
}

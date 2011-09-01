package com.polopoly.ps.pcmd.tool.graphcontent;

import com.polopoly.ps.pcmd.tool.graphcontent.model.ContentGraph;
import com.polopoly.ps.pcmd.tool.graphcontent.model.ContentNode;
import com.polopoly.ps.pcmd.tool.graphcontent.model.Reference;
import com.polopoly.ps.pcmd.util.dot.EdgeStyle;
import com.polopoly.ps.pcmd.util.dot.GraphStyle;
import com.polopoly.ps.pcmd.util.dot.NodeStyle;
import com.polopoly.ps.pcmd.util.dot.GraphStyle.RankDirection;
import com.polopoly.ps.pcmd.util.dot.NodeStyle.Style;

public class DefaultGraphStyler implements DotGraphStyler {
    public GraphStyle style(ContentGraph graph) {
        return new GraphStyle().rankDirection(RankDirection.LEFT_TO_RIGHT);
    }

    public NodeStyle style(ContentNode contentNode) {
        NodeStyle style = new NodeStyle();
        
        if (contentNode.hasTag("major:14")) {
            style.style(Style.FILLED).fillColor("gold");
        } else if (contentNode.hasTag("major:15")) {
            style.style(Style.FILLED).fillColor("darksalmon");
        } else if (contentNode.hasTag("major:2")) {
            style.style(Style.FILLED).fillColor("burlywood1");
        }
        
        if (contentNode.hasTag("origin")) {
            style.penWidth(2.0);
        }
        
        return style;
    }

    public EdgeStyle style(Reference ref) {
        EdgeStyle style = new EdgeStyle();
        if (ref.hasTag("inputTemplate")) {
            style = style.style(EdgeStyle.Style.DASHED);
        } else if (ref.hasTag("rmd")) {
            style = style.style(EdgeStyle.Style.DOTTED);
        }
        return style;
    }
}

package com.polopoly.ps.pcmd.tool.graphcontent;

import com.polopoly.ps.pcmd.tool.graphcontent.model.ContentGraph;
import com.polopoly.ps.pcmd.tool.graphcontent.model.ContentNode;
import com.polopoly.ps.pcmd.tool.graphcontent.model.Reference;
import com.polopoly.ps.pcmd.util.dot.EdgeStyle;
import com.polopoly.ps.pcmd.util.dot.GraphStyle;
import com.polopoly.ps.pcmd.util.dot.NodeStyle;

public interface DotGraphStyler {
    GraphStyle style(ContentGraph graph);
    NodeStyle style(ContentNode contentNode);
    EdgeStyle style(Reference ref);
}

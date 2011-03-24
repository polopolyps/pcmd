package com.polopoly.pcmd.tool.graphcontent;

import com.polopoly.pcmd.tool.graphcontent.model.ContentGraph;
import com.polopoly.pcmd.tool.graphcontent.model.ContentNode;
import com.polopoly.pcmd.tool.graphcontent.model.Reference;
import com.polopoly.pcmd.util.dot.EdgeStyle;
import com.polopoly.pcmd.util.dot.GraphStyle;
import com.polopoly.pcmd.util.dot.NodeStyle;

public interface DotGraphStyler {
    GraphStyle style(ContentGraph graph);
    NodeStyle style(ContentNode contentNode);
    EdgeStyle style(Reference ref);
}

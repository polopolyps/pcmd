package com.polopoly.ps.pcmd.tool.graphcontent;

import java.io.PrintStream;

import com.polopoly.ps.pcmd.tool.graphcontent.model.ContentGraph;

public interface GraphRenderer {
    void render(ContentGraph graph, PrintStream output) throws Exception;
}

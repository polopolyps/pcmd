package com.polopoly.pcmd.tool.graphcontent;

import java.io.PrintStream;

import com.polopoly.pcmd.tool.graphcontent.model.ContentGraph;

public interface GraphRenderer {
    void render(ContentGraph graph, PrintStream output) throws Exception;
}

package com.polopoly.ps.pcmd.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.polopoly.ps.pcmd.tool.graphcontent.DefaultGraphStyler;
import com.polopoly.ps.pcmd.tool.graphcontent.DotGraphRenderer;
import com.polopoly.ps.pcmd.tool.graphcontent.GraphBuilder;
import com.polopoly.ps.pcmd.tool.graphcontent.GraphContentParameters;
import com.polopoly.ps.pcmd.tool.graphcontent.GraphRenderer;
import com.polopoly.ps.pcmd.tool.graphcontent.TextGraphRenderer;
import com.polopoly.ps.pcmd.tool.graphcontent.GraphContentParameters.FORMAT;
import com.polopoly.ps.pcmd.tool.graphcontent.model.ContentGraph;
import com.polopoly.util.client.PolopolyContext;

public class GraphContentTool implements Tool<GraphContentParameters> {
    public GraphContentParameters createParameters() {
        return new GraphContentParameters();
    }

    public void execute(PolopolyContext context, GraphContentParameters parameters) {
        if (!parameters.isForce()) {
            if (!ensureContentSafety(parameters)) {
                return;
            }
        }

        ContentGraph graph = new ContentGraph(true);

        GraphBuilder grapher = new GraphBuilder(context, parameters, graph);
        grapher.fillWith(parameters.getContentIds());
        
        GraphRenderer renderer = setUpRenderer(context, parameters);
        
        try {
            System.err.println("Graphing " + graph.getContentNodes().size() + " content nodes...");
            renderer.render(graph, System.out);
        } catch (Exception e) {
            throw new RuntimeException("Could not write dot syntax", e);
        }
    }

    private boolean ensureContentSafety(GraphContentParameters params) {
        if (params.getDepth() > 3) {
            if (params.getFilterMajors() == null) {
                System.err.println("You have selected a depth greater than 3 and have not specified any filter majors. "
                        + "Since this could potentially mean huge amounts of processing and output, "
                        + "you are required to use the --force flag");
                return false;
            }

            List<Integer> unsafeMajors = new ArrayList<Integer>(params.getFilterMajors());
            unsafeMajors.removeAll(Arrays.asList(0, 2, 10, 11, 14, 15, 17));
            if (unsafeMajors.size() > 0) {
                System.err.println("You have selected a depth greater than 3 and have included filter majors "
                        + "that are considered unsafely big in common circumstances. "
                        + "Since this could potentially mean huge amounts of processing and output, "
                        + "you are required to use the --force flag");
                return false;
            }
        }
        return true;
    }

    private GraphRenderer setUpRenderer(PolopolyContext context, GraphContentParameters parameters) {
        if (parameters.getFormat() == FORMAT.Dot) {
            return new DotGraphRenderer(context, new DefaultGraphStyler());
        } else {
            return new TextGraphRenderer(context);
        }
    }

    public String getHelp() {
        return "Prints a text or GraphViz \"dot\" representation on stdout with a graph of content and content references. " +
                "You can provide specific content ids on the command line or stdin.\n";
    }
}

package com.polopoly.ps.pcmd.util.dot;

import java.io.PrintStream;

public class DotWriter {
    private PrintStream output;
    private GraphStyle style;

    public DotWriter(PrintStream output) {
        this.output = output;
        this.style = null;
    }
    
    public DotWriter(PrintStream output, GraphStyle style) {
        this.output = output;
        this.style = style;
    }
    
    public void begin() {
        output.println("digraph \"G\" {");
        if (style != null) {
            output.println("graph " + style.render());
        }
    }
    
    public void end() {
        output.println("}");
        output.flush();
    }
    
    public void printNode(String name) { printNode(name, null); }
    
    public void printNode(String name, NodeStyle style) {
        output.print("\t\"" + name + "\"");
        if (style != null) {
            output.print(" " + style.render());
        }
        
        output.println(";");
    }
    
    public void printEdge(String from, String to) { printEdge(from, to, null); }
    
    public void printEdge(String from, String to, EdgeStyle style) {
        output.print("\t\"" + from + "\" -> \"" + to + "\"");
        if (style != null) {
            output.print(" " + style.render());
        }
        
        output.println(";");
    }
}

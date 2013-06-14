package com.polopoly.ps.pcmd.text;

import java.util.ArrayList;
import java.util.List;

import com.polopoly.ps.pcmd.topologicalsort.Edge;
import com.polopoly.ps.pcmd.topologicalsort.SingleEdge;
import com.polopoly.ps.pcmd.topologicalsort.Vertex;

public class TextContentVertex implements Vertex<TextContentVertex> {
    private final List<Edge<TextContentVertex>> dependencies = new ArrayList<Edge<TextContentVertex>>();
    private final String id;
    private final TextContent textContent;

    public TextContentVertex(String id, TextContent textContent) {
        this.id = id;
        this.textContent = textContent;
    }

    public Iterable<Edge<TextContentVertex>> getEdges() {
        return dependencies;
    }

    public String getId() {
        return id;
    }

    public TextContent getTextContent() {
        return textContent;
    }

    public void addDependency(TextContentVertex textContentVertex) {
        dependencies.add(new SingleEdge<TextContentVertex>(textContentVertex));
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TextContentVertex other = (TextContentVertex) obj;
        return id.equals(other.id);
    }
}

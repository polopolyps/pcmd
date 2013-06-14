package com.polopoly.ps.pcmd.topologicalsort;

import java.util.Collections;
import java.util.Set;

public class SingleEdge<T> implements Edge<T> {
    private Vertex<T> fromVertex;
    private String description;

    public SingleEdge(Vertex<T> fromVertex) {
        this.fromVertex = fromVertex;
    }

    public Iterable<Vertex<T>> getFromVertexes() {
        return Collections.singleton(fromVertex);
    }

    public boolean isFromAny(Set<Vertex<T>> vertexes) {
        return vertexes.contains(fromVertex);
    }

    @Override
    public String toString() {
        return fromVertex.toString() + (description != null ? " (" + description + ")" : "");
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SingleEdge && ((SingleEdge<?>) o).fromVertex.equals(fromVertex);
    }

    @Override
    public int hashCode() {
        return fromVertex.hashCode();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

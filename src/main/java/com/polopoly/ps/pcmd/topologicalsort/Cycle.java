package com.polopoly.ps.pcmd.topologicalsort;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class Cycle<T> {
    private Set<Vertex<T>> vertexesInvolved;
    private Collection<Edge<T>> edgesInvolved;

    public Cycle(Set<Vertex<T>> vertexesInvolved, Collection<Edge<T>> edgesInvolved) {
        this.vertexesInvolved = vertexesInvolved;
        this.edgesInvolved = edgesInvolved;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer(100);
        Iterator<Edge<T>> edgeIterator = edgesInvolved.iterator();

        for (Vertex<T> vertex : vertexesInvolved) {
            if (result.length() > 0) {
                result.append(", ");
            }

            result.append(vertex.toString());

            Edge<T> edge = edgeIterator.next();

            if (edge.getDescription() != null) {
                result.append(" (" + edge.getDescription() + ")");
            }
        }

        return result.toString();
    }

    public Vertex<T> firstVertex() {
        return vertexesInvolved.iterator().next();
    }
}

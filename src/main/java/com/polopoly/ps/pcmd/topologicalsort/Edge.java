package com.polopoly.ps.pcmd.topologicalsort;

import java.util.Set;

/**
 * We allow an edge to be from several vertexes. The interpretation is that only
 * one of the vertexes the edge is from needs to satisfied in the topological
 * sort (a kind of or relationship). This is used to represent content
 * references to objects that are defined and redefined in several files.
 */
public interface Edge<T> {
    boolean isFromAny(Set<Vertex<T>> vertexes);

    Iterable<Vertex<T>> getFromVertexes();

    String getDescription();
}

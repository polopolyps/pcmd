package com.polopoly.ps.pcmd.topologicalsort;

public interface Vertex<T> {
    Iterable<Edge<T>> getEdges();
}

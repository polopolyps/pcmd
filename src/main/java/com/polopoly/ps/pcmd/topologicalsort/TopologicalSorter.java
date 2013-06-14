package com.polopoly.ps.pcmd.topologicalsort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TopologicalSorter<T extends Vertex<T>> {
    private static final Logger logger = Logger.getLogger(TopologicalSorter.class.getName());

    private LinkedHashSet<Vertex<T>> resolved = new LinkedHashSet<Vertex<T>>();
    private LinkedHashSet<Vertex<T>> remaining = new LinkedHashSet<Vertex<T>>();

    private boolean breakOnCycle;

    public TopologicalSorter(List<T> vertexesAsObjects) {
        remaining.addAll(vertexesAsObjects);
    }

    public List<T> sort() {
        while (notDone()) {
            boolean found = false;

            for (Vertex<T> remainingVertex : remaining) {
                if (canBePicked(remainingVertex)) {
                    pick(remainingVertex);
                    found = true;
                    break;
                }
            }

            if (!found) {
                if (breakOnCycle) {
                    break;
                }

                resolveCycle();
            }
        }

        return resolvedAsList();
    }

    @SuppressWarnings("unchecked")
    private List<T> resolvedAsList() {
        ArrayList<T> result = new ArrayList<T>(resolved.size());

        for (Vertex<T> t : resolved) {
            result.add((T) t);
        }

        return result;
    }

    private void resolveCycle() {
        Cycle<T> cycle = findCycle();

        logger.log(Level.WARNING, "There is a cyclical dependency among the vertexes: " + cycle);

        pick(cycle.firstVertex());
    }

    public void setBreakOnCycle(boolean breakOnCycle) {
        this.breakOnCycle = breakOnCycle;
    }

    Cycle<T> findCycle() {
        return findCycle(getFirstUnpicked());
    }

    Cycle<T> findCycle(Vertex<T> startVertex) {
        LinkedHashSet<Vertex<T>> vertexesInvolved = new LinkedHashSet<Vertex<T>>();
        Collection<Edge<T>> edgesInvolved = new ArrayList<Edge<T>>();

        vertexesInvolved.add(startVertex);

        try {
            findCycle(startVertex, vertexesInvolved, edgesInvolved);
        } catch (CycleDetectedException e) {
            Iterator<Edge<T>> edgeIterator = edgesInvolved.iterator();
            Iterator<Vertex<T>> vertexIterator = vertexesInvolved.iterator();

            while (vertexIterator.hasNext()) {
                if (vertexIterator.next() == e.getDetectedAt()) {
                    break;
                } else {
                    vertexIterator.remove();
                    edgeIterator.next();
                    edgeIterator.remove();
                }
            }
        }

        return new Cycle<T>(vertexesInvolved, edgesInvolved);
    }

    private void findCycle(Vertex<T> t, Set<Vertex<T>> vertexesInvolved, Collection<Edge<T>> edgesInvolved)
        throws CycleDetectedException {
        for (Edge<T> edge : t.getEdges()) {
            for (Vertex<T> dependency : edge.getFromVertexes()) {
                if (!remaining.contains(dependency)) {
                    continue;
                }

                edgesInvolved.add(edge);
                boolean alreadyAdded = !vertexesInvolved.add(dependency);

                if (alreadyAdded) {
                    throw new CycleDetectedException(dependency);
                }

                findCycle(dependency, vertexesInvolved, edgesInvolved);
            }
        }
    }

    private Vertex<T> getFirstUnpicked() {
        Iterator<Vertex<T>> remainingIterator = remaining.iterator();

        if (!remainingIterator.hasNext()) {
            throw new IllegalStateException("There is no unpicked vertex.");
        }

        return remainingIterator.next();
    }

    private void pick(Vertex<T> vertex) {
        logger.log(Level.FINE, "Picked " + vertex + " as vertex " + resolved.size() + ".");

        remaining.remove(vertex);
        resolved.add(vertex);
    }

    private boolean canBePicked(Vertex<T> vertex) {
        for (Edge<T> edge : vertex.getEdges()) {
            if (!edge.isFromAny(resolved)) {
                return false;
            }
        }

        return true;
    }

    private boolean notDone() {
        return !remaining.isEmpty();
    }
}

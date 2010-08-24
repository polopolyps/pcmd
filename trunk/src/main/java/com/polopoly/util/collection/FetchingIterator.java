package com.polopoly.util.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An abstract {@link Iterator} implementation that requests the next object
 * from an abstract method and caches it between successive calls to
 * {@link #hasNext()}.
 * 
 * @author AndreasE
 */
public abstract class FetchingIterator<T> implements Iterator<T> {
    private T next;

    /**
     * Should return the next object in the iteration. Returns null if there are
     * no more objects.
     */
    protected abstract T fetch();

    public void remove() {
    }

    public boolean hasNext() {
        if (next == null) {
            next = fetch();
        }

        return next != null;
    }

    public T next() {
        if (next == null) {
            next = fetch();
        }

        if (next == null) {
            throw new NoSuchElementException("Passed end of iteration.");
        }

        try {
            return next;
        } finally {
            next = null;
        }
    }
}
package com.polopoly.ps.pcmd.util;

import java.util.Iterator;

/**
 * An Iterator returning first the elements of one Iterator and then, when they
 * are finished, those of another.
 * @author AndreasE
 */
public class JoiningIterator<T> implements Iterator<T> {
    private Iterator<T> iterator;
    private Iterator<T> iterator2;

    /**
     * Create an iterator containing the elements of first iterator, then iterator2.
     */
    public JoiningIterator(Iterator<T> iterator, Iterator<T> iterator2) {
        this.iterator = iterator;
        this.iterator2 = iterator2;
    }

    public boolean hasNext() {
        return iterator.hasNext() || (iterator2 != null && iterator2.hasNext());
    }

    public T next() {
        if (iterator.hasNext()) {
            return iterator.next();
        }
        else {
            iterator = iterator2;
            iterator2 = null;
            return iterator.next();
        }
    }

    public void remove() {
        iterator.remove();
    }
}

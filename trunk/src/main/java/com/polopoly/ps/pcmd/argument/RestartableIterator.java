package com.polopoly.ps.pcmd.argument;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RestartableIterator<T> implements Iterable<T> {
    private List<T> returned = new ArrayList<T>();
    private Iterator<T> delegate;

    public RestartableIterator(Iterator<T> iterator) {
        this.delegate = iterator;
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int atReturned;

            public boolean hasNext() {
                return atReturned < returned.size() || delegate.hasNext();
            }

            public T next() {
                if (atReturned < returned.size()) {
                    return returned.get(atReturned++);
                }

                T result = delegate.next();

                returned.add(result);
                atReturned++;

                return result;
            }

            public void remove() {

            }
        };
    }

}

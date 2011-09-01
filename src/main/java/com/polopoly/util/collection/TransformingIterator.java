package com.polopoly.util.collection;

import java.util.Iterator;

public abstract class TransformingIterator<T, U> implements Iterator<U> {
    private Iterator<T> delegate;

    public TransformingIterator(Iterator<T> delegate) {
        this.delegate = delegate;
    }

    public boolean hasNext() {
        return delegate.hasNext();
    }

    public U next() {
        return transform(delegate.next());
    }

    protected abstract U transform(T next);

    public void remove() {
        delegate.remove();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}

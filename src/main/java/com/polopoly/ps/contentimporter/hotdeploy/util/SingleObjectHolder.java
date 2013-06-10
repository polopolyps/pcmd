package com.polopoly.ps.contentimporter.hotdeploy.util;

public class SingleObjectHolder<T> {
    protected T heldObject;

    public SingleObjectHolder(T object) {
        this.heldObject = object;
    }

    protected void setHeldObject(T heldObject) {
        this.heldObject = heldObject;
    }

    @Override
    public boolean equals(Object o) {
        return o.getClass().equals(getClass()) && ((SingleObjectHolder<?>) o).heldObject.equals(heldObject);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() + heldObject.hashCode();
    }

    @Override
    public String toString() {
        return "holder of " + heldObject;
    }
}

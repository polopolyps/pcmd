package com.polopoly.ps.contentimporter.hotdeploy.util;

public class Tuple<S1, S2> {
    private S1 object1;
    private S2 object2;

    public Tuple(S1 object1, S2 object2) {
        this.object1 = object1;
        this.object2 = object2;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(getClass()) &&
            ((Tuple<?,?>) obj).object1.equals(object1) &&
            ((Tuple<?,?>) obj).object2.equals(object2);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() + object1.hashCode() + object2.hashCode();
    }

    @Override
    public String toString() {
        return "<" + object1 + "," + object2 + ">";
    }
}

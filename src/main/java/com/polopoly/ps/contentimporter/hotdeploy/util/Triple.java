package com.polopoly.ps.contentimporter.hotdeploy.util;

public class Triple<S1, S2, S3> {
    private S1 object1;
    private S2 object2;
    private S3 object3;

    public Triple(S1 object1, S2 object2, S3 object3) {
        this.object1 = object1;
        this.object2 = object2;
        this.object3 = object3;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(getClass()) &&
            equals(((Triple<?,?,?>) obj).object1, object1) &&
            equals(((Triple<?,?,?>) obj).object2, object2) &&
            equals(((Triple<?,?,?>) obj).object3, object3);
    }

    private static boolean equals(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        else {
            return o1.equals(o2);
        }
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() + object1.hashCode() + object2.hashCode();
    }

    @Override
    public String toString() {
        return "<" + object1 + "," + object2 + "," + object3 + ">";
    }
}

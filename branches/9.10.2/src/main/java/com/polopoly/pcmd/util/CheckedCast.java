package com.polopoly.pcmd.util;


/**
 * Methods performing casts throwing a checked exception rather than the
 * unchecked ClassCastException.
 */
public class CheckedCast {
    /**
     * If the object is compatible with the specified class, returns it.
     * Otherwise, throws a CheckedClassCastException.
     * @param object The object to cast.
     * @param klass The class to cast it to.
     * @return The object, if it was of the right type.
     * @throws CheckedClassCastException If it was not.
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object object, Class<T> T) throws CheckedClassCastException {
        if (object != null && T.isAssignableFrom(object.getClass())) {
            return (T) object;
        } else {
            throw CheckedClassCastException.create(object, T);
        }
    }

    /**
     * If the object is compatible with the specified class, returns it.
     * Otherwise, throws a CheckedClassCastException.
     * @param object The object to cast.
     * @param klass The class to cast it to.
     * @param objectName A description of the object (used in the error message).
     * @return The object, if it was of the right type.
     * @throws CheckedClassCastException If it was not.
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object object, Class<T> T, String objectName) throws CheckedClassCastException {
        if (object != null && T.isAssignableFrom(object.getClass())) {
            return (T) object;
        } else {
            throw CheckedClassCastException.create(object, T, objectName);
        }
    }
}

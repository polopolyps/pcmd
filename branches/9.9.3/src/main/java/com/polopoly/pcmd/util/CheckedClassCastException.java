package com.polopoly.pcmd.util;

import com.polopoly.cm.policy.Policy;

/** Thrown by the {@link CheckedCast} methods if an object is not of the expected type.
 */
public class CheckedClassCastException extends Exception {
    private static final String DEFAULT_OBJECT_NAME = "object";

    /**
     * Create a new exception with the specified message.
     */
    public CheckedClassCastException(String message) {
        super(message);
    }

    /**
     * Create a CheckedClassCastException
     * @param object The object that was supposed to be cast.
     * @param klass The type the object should have had.
     * @param objectName A descriptive name for the object.
     */
    public static CheckedClassCastException create(Object object, Class<? extends Object> klass) {
        return create(object, klass, null);
    }

    /**
     * Create a CheckedClassCastException
     * @param object The object that was supposed to be cast.
     * @param klass The type the object should have had.
     * @param objectName A descriptive name for the object.
     */
    public static CheckedClassCastException create(Object object, Class<? extends Object> klass, String objectName) {
        StringBuffer message = new StringBuffer(100);

        if (objectName != null) {
            message.append(objectName);
        }
        else if (object instanceof Policy) {
            message.append(((Policy) object).getContentId().getContentId());
        }
        else {
            message.append(DEFAULT_OBJECT_NAME);
        }

        message.append(" was ");

        if (object == null) {
            message.append("null ");
        } else {
            message.append("of type " + object.getClass().getName() + " ");
        }
        message.append("rather than the expected type " + klass.getName() + ".");

        return new CheckedClassCastException(message.toString());
    }
}

package com.polopoly.ps.contentimporter.hotdeploy.util;

/** 
 * Thrown by the {@link CheckedCast} methods if an object is not of the expected type.
 */
@SuppressWarnings("serial")
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
    public static CheckedClassCastException create(Object object, Class<?> klass) {
    	return create(object, klass, null);
    }
	
    /**
     * Create a CheckedClassCastException
     * @param object The object that was supposed to be cast.
     * @param klass The type the object should have had.
     * @param objectName A descriptive name for the object.
     */
    public static CheckedClassCastException create(Object object, Class<?> klass, String objectName) {
        StringBuffer message = new StringBuffer(100);

        if (objectName != null) {
        	message.append(objectName);
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

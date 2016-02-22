package com.polopoly.ps.service;

/**
 * Marker interface for service implementations that work as wrappers for
 * services. They are are expected to implement the service interface and to
 * pass on all calls to the delegate (which is the real service). Wrappers can
 * e.g. do logging or error handling.
 */
public interface ServiceWrapper<S extends Service> {
    void setDelegate(S service);

    /**
     * Does this wrapper want to be before the specified other wrapper?
     */
    ServiceWrapperRelativeOrder getWrapperOrder(ServiceWrapper<S> otherWrapper);

    /**
     * This can be used in conjuction with
     * ServiceWrapperRelativeOrder.USE_SORT_INDEX
     */
    int getWrapperIndex();
}

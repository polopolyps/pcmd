package com.polopoly.util.exception;

import com.polopoly.cm.client.CMException;

public class NoSuchExternalIdException extends Exception {

    public NoSuchExternalIdException(String externalId, CMException e) {
        super("The external ID \"" + externalId + "\" could not be resolved", e);
    }

    public NoSuchExternalIdException(String externalId) {
        super("The external ID \"" + externalId + "\" could not be resolved");
    }
}

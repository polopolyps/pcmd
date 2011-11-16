package com.polopoly.util.exception;

import com.polopoly.cm.client.CMException;

/**
 * The external ID that was to be set was already in use by another object. 
 */
public class ExternalIdAlreadyInUseException extends CMException {

	public ExternalIdAlreadyInUseException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExternalIdAlreadyInUseException(String message) {
		super(message);
	}

}

package com.polopoly.util.exception;

import com.polopoly.cm.client.CMException;

/**
 * The content object already had an external ID which cannot be reassigned.
 */
public class ExternalIdAlreadySetException extends CMException {

	public ExternalIdAlreadySetException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExternalIdAlreadySetException(String message) {
		super(message);
	}

}

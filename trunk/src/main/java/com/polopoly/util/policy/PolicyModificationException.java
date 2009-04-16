package com.polopoly.util.policy;

import com.polopoly.cm.client.CMException;

public class PolicyModificationException extends CMException {

	public PolicyModificationException(String message) {
		super(message);
	}

	public PolicyModificationException(String message, Exception cause) {
		super(message, cause);
	}

}

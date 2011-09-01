package com.polopoly.util.exception;

import com.polopoly.cm.client.CMRuntimeException;

public class ServiceUnattachedException extends CMRuntimeException {

	public ServiceUnattachedException(String serviceName) {
		super(serviceName + " had not been attached to the PolopolyContext. " +
				"It must be explicitly attached when connecting.");
	}


}

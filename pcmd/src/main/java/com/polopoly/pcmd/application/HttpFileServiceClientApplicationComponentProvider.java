package com.polopoly.pcmd.application;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.application.Application;
import com.polopoly.application.IllegalApplicationStateException;
import com.polopoly.cm.client.HttpFileServiceClient;
import com.polopoly.ps.pcmd.ApplicationComponentProvider;

public class HttpFileServiceClientApplicationComponentProvider implements ApplicationComponentProvider {

	private static final Logger LOGGER = Logger.getLogger(HttpFileServiceClientApplicationComponentProvider.class.getName());

	@Override
	public void add(Application app) throws IllegalApplicationStateException {
		try {
			app.addApplicationComponent(new HttpFileServiceClient());
		} catch (NoClassDefFoundError e) {
			LOGGER.log(Level.WARNING, "A class is missing: " + e.getMessage());
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HttpFileServiceClientApplicationComponentProvider");
		return builder.toString();
	}

	
}

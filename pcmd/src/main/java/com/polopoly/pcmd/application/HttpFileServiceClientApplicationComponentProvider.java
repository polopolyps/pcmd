package com.polopoly.pcmd.application;

import com.polopoly.application.Application;
import com.polopoly.application.IllegalApplicationStateException;
import com.polopoly.cm.client.HttpFileServiceClient;
import com.polopoly.ps.pcmd.ApplicationComponentProvider;

public class HttpFileServiceClientApplicationComponentProvider implements ApplicationComponentProvider {

	@Override
	public void add(Application app) throws IllegalApplicationStateException {
		app.addApplicationComponent(new HttpFileServiceClient());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HttpFileServiceClientApplicationComponentProvider");
		return builder.toString();
	}

	
}

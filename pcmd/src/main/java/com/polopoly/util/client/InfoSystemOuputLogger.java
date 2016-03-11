package com.polopoly.util.client;

public class InfoSystemOuputLogger extends DebugSystemOuputLogger {

	public void info(String logMessage) {
		System.err.println(logMessage);
	}

	@Override
	public void debug(String logMessage) {
		//nothing
	}

	@Override
	public void error(String logMessage) {
		System.err.println("ERROR: " + logMessage);
	}

}

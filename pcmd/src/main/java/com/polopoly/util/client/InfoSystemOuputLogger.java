package com.polopoly.util.client;

public class InfoSystemOuputLogger extends DebugSystemOuputLogger {

	public void info(String logMessage) {
		System.out.println(logMessage);
	}

	@Override
	public void debug(String logMessage) {
		//nothing
	}

	@Override
	public void error(String logMessage, Throwable e) {
		System.err.println("ERROR: " + logMessage);
	}

}

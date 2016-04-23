package com.polopoly.util.client;

public class DebugSystemOuputLogger implements PolopolyClientLogger {

	public void info(String logMessage) {
		System.err.println(logMessage);
	}

	@Override
	public void debug(String logMessage) {
		System.err.println("DEBUG: " + logMessage);
	}

	@Override
	public void error(String logMessage, Throwable e) {
		System.err.println("ERROR: " + logMessage);
		e.printStackTrace(System.err);
	}

}

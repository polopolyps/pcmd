package com.polopoly.ps.pcmd.jstackparser;

public class JStackParseException extends Exception {

	public JStackParseException() {
		super();
	}

	public JStackParseException(LineCountingBufferedReader reader, String message) {
		super("On line " + reader.getLineNumber() + " (\"" + reader.getCurrentLine() + "\"): " + message);
	}
}

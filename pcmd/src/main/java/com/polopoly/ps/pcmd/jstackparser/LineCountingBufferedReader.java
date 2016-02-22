package com.polopoly.ps.pcmd.jstackparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class LineCountingBufferedReader extends BufferedReader {
	private int lineNumber;
	private String currentLine;

	private String pushed;

	public String getCurrentLine() {
		return currentLine;
	}

	public LineCountingBufferedReader(Reader arg0) {
		super(arg0);
	}

	@Override
	public String readLine() throws IOException {
		if (pushed != null) {
			try {
				return pushed;
			} finally {
				pushed = null;
			}
		}

		boolean success = false;

		try {
			currentLine = super.readLine();

			if (currentLine != null) {
				success = true;
			}

			return currentLine;
		} finally {
			if (success) {
				lineNumber++;
			}
		}
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void push(String line) {
		pushed = line;
	}
}

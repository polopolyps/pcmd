package com.polopoly.ps.pcmd.jstackparser;

import java.util.ArrayList;
import java.util.List;

public class JStackThread {
	private List<String> stackTrace = new ArrayList<String>();
	private String id;
	private JStack stack;

	public JStackThread(JStack stack, String id) {
		this.id = id;
		this.stack = stack;
	}

	public JStack getStack() {
		return stack;
	}

	public String getId() {
		return id;
	}

	public String getStackTrace(int i) {
		return stackTrace.get(i);
	}

	public void addStackTrace(String line) {
		stackTrace.add(line);
	}

	public int getLastCommonLine(JStackThread thread) throws NoCommonLineException {
		int i = 0;

		int minlength = Math.min(thread.length(), length());

		if (minlength == 0) {
			throw new NoCommonLineException();
		}

		for (; i < minlength; i++) {
			if (!thread.getStackTraceFromEnd(i).equals(getStackTraceFromEnd(i))) {
				if (i > 0) {
					return length() - i - 1;
				} else {
					throw new NoCommonLineException();
				}
			}
		}

		return length() - i;
	}

	public String getStackTraceFromEnd(int i) {
		return getStackTrace(length() - i - 1);
	}

	private int length() {
		return stackTrace.size();
	}

	public List<String> getStackTrace() {
		return stackTrace;
	}
}

package com.polopoly.ps.pcmd.tool;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.ps.pcmd.jstackparser.JStack;
import com.polopoly.ps.pcmd.jstackparser.JStackParseException;
import com.polopoly.ps.pcmd.jstackparser.JStackParser;
import com.polopoly.ps.pcmd.jstackparser.JStackThread;
import com.polopoly.ps.pcmd.jstackparser.NoCommonLineException;
import com.polopoly.ps.pcmd.jstackparser.ThreadVisitor;
import com.polopoly.util.client.PolopolyContext;

public class RegularJstackReportTool implements Tool<RegularJstackReportParameters>, DoesNotRequireRunningPolopoly {
	private static final String[] BORING = new String[] { "sun.net.www.http.HttpClient.closeServer",
			"java.lang.Object.wait", "java.lang.ref.ReferenceQueue.enqueue", "java.lang.Thread.sleep",
			"java.net.PlainSocketImpl.socketAccept", "sun.misc.Unsafe.park", "java.util.concurrent.ThreadPoolExecutor",
			"java.util.concurrent.ThreadPoolExecutor.getTask", "java.util.concurrent.locks.AbstractQueuedSynchronizer",
			"java.net.ServerSocket.accept", "org.apache.coyote.http11.Http11Processor.process",
			"sun.management.jmxremote.LocalRMIServerSocketFactory", "org.apache.tomcat.util.net.JIoEndpoint$Worker.run" };

	public class SuspiciousThread {
		JStackThread firstThread;
		JStackThread lastThread;

		public SuspiciousThread(JStack jstack, JStackThread thread) {
			this.firstThread = thread;
		}

	}

	public class LongRunningThreadFinderVisitor implements ThreadVisitor {

		private JStack lastStack;
		private Map<String, SuspiciousThread> suspiciousThreads = new HashMap<String, SuspiciousThread>();
		private boolean printStack;

		public LongRunningThreadFinderVisitor(boolean printStack) {
			this.printStack = printStack;
		}

		@Override
		public void visit(JStack stack) {
			if (lastStack == null) {
				lastStack = stack;

				return;
			}

			for (JStackThread thread : stack) {
				SuspiciousThread lastSuspicious = suspiciousThreads.get(thread.getId());

				if (lastSuspicious == null) {
					lastSuspicious = new SuspiciousThread(stack, thread);
					suspiciousThreads.put(thread.getId(), lastSuspicious);
					continue;
				}

				boolean stillRunningAndInteresting = false;

				try {
					String lastCommon = lastSuspicious.firstThread.getStackTrace(lastSuspicious.firstThread
							.getLastCommonLine(thread));

					stillRunningAndInteresting = !isBoring(lastCommon, lastSuspicious.firstThread);
				} catch (NoCommonLineException e) {
				}

				if (stillRunningAndInteresting) {
					lastSuspicious.lastThread = thread;
				} else {
					if (lastSuspicious.lastThread != null) {
						potentialMatch(stack, thread, lastSuspicious);
					}

					lastSuspicious = new SuspiciousThread(stack, thread);
					suspiciousThreads.put(thread.getId(), lastSuspicious);
				}
			}

			lastStack = stack;
		}

		private boolean isCacheUpdate(JStackThread thread) {
			for (String line : thread.getStackTrace()) {
				if (line.contains("ChangelistCacheUpdater.updateC")) {
					return true;
				}
			}

			return false;
		}

		protected void potentialMatch(JStack stack, JStackThread thread, SuspiciousThread lastSuspicious) {
			try {
				int lastCommonLine = lastSuspicious.firstThread.getLastCommonLine(lastSuspicious.lastThread);
				String stackTrace = lastSuspicious.firstThread.getStackTrace(lastCommonLine);

				if (!isBoring(stackTrace, lastSuspicious.firstThread)) {
					long startTime = lastSuspicious.firstThread.getStack().getDate().getTime();
					long lastTime = lastSuspicious.lastThread.getStack().getDate().getTime();
					long laterTime = stack.getDate().getTime();

					System.out.print(lastSuspicious.firstThread.getStack().getDate() + ": Thread " + thread.getId()
							+ " ran at for " + (lastTime - startTime) + " - " + (laterTime - startTime) + " ms");

					if (printStack) {
						System.out.println();

						for (int i = lastCommonLine; i < lastSuspicious.firstThread.getStackTrace().size(); i++) {
							System.out.println("   at " + lastSuspicious.firstThread.getStackTrace(i));
						}
					} else {
						System.out.println(": " + stackTrace);
					}
				}
			} catch (NoCommonLineException e1) {
				// should not be possible.
				e1.printStackTrace();
			}
		}

		private boolean isBoring(String stackTrace, JStackThread thread) {
			for (String boring : BORING) {
				if (stackTrace.contains(boring)) {
					return true;
				}
			}

			if (stackTrace.contains("java.net.SocketInputStream.socketRead0") && thread.getStackTrace().size() < 20) {
				return true;
			}

			return false;
		}

	}

	@Override
	public void execute(PolopolyContext context, RegularJstackReportParameters parameters) throws FatalToolException {

		try {
			FileReader reader = new FileReader(parameters.getFile());

			new JStackParser().parse(reader, new LongRunningThreadFinderVisitor(parameters.isPrintStackTrace()));
		} catch (FileNotFoundException e) {
			throw new FatalToolException(e);
		} catch (JStackParseException e) {
			throw new FatalToolException(e);
		}
	}

	@Override
	public RegularJstackReportParameters createParameters() {
		return new RegularJstackReportParameters();
	}

	@Override
	public String getHelp() {
		return "Goes through a file with a set of stack traces in chronological order and find long-running tasks.";
	}

}

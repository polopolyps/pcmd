package com.polopoly.ps.pcmd.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.policy.Policy;
import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.exception.ContentGetException;
import com.polopoly.util.exception.NoSuchExternalIdException;

public class MonitorTool implements Tool<MonitorParameters> {

	public class JStackThread extends Thread {

		@Override
		public void run() {
			while (isRunning) {
				long commitLength = System.currentTimeMillis() - commitStart;

				int sleep = parameters.getLimit() / 10;

				if (commitStart > 0 && commitLength > parameters.getLimit()) {
					System.out.println(DATE_FORMAT.format(new Date())
							+ " Triggering jstack since current commit had been running for " + commitLength + " ms.");

					jstack();

					sleep = 5000;
				}

				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
					// fine.
				}
			}
		}

	}

	private static final String INPUT_TEMPLATE = "p.DefaultArticle";
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private volatile long commitStart;

	private JStackThread jstackThread;
	private MonitorParameters parameters;
	private boolean isRunning = true;
	private Object hostname;

	@Override
	public void execute(PolopolyContext context, MonitorParameters parameters) throws FatalToolException {
		try {
			hostname = System.getenv("COLLECTD_HOSTNAME");

			if (hostname == null) {
				InetAddress addr = InetAddress.getLocalHost();
				hostname = addr.getHostName();
			}
		} catch (UnknownHostException e1) {
			hostname = "unknown";
		}

		this.parameters = parameters;

		if (parameters.getJstackPid() > 0) {
			jstackThread = new JStackThread();

			jstackThread.start();
		}

		try {
			for (int i = parameters.getRepeat() - 1; i >= 0; i--) {
				singleCommit(context, parameters);

				if (i > 0) {
					try {
						Thread.sleep(parameters.getSleep());
					} catch (InterruptedException e) {
						// fine.
					}
				}
			}
		} finally {
			isRunning = false;
		}
	}

	protected void singleCommit(PolopolyContext context, MonitorParameters parameters) {
		commitStart = System.currentTimeMillis();

		ContentRead content;
		Policy policy;

		try {
			try {
				content = context.getContent(new ExternalContentId(getExternalId()));

				policy = context.getPolicyCMServer().createContentVersion(content.getContentId());
			} catch (ContentGetException e) {
				VersionedContentId templateId;

				templateId = context.resolveExternalId(INPUT_TEMPLATE);

				policy = context.getPolicyCMServer().createContent(17, templateId);
				policy.getContent().setName("PCMD Commmit Time Monitor Content");
				policy.getContent().setExternalId(getExternalId());
			}

			policy.getContent().commit();
		} catch (CMException e) {
			System.out.println("99999");

			throw new FatalToolException(e.toString(), e);
		} catch (NoSuchExternalIdException e) {
			System.out.println("99999");

			throw new FatalToolException(e.toString(), e);
		}

		long commitTime = System.currentTimeMillis() - commitStart;
		commitStart = 0;

		if (!parameters.isOnlySlow() || commitTime >= parameters.getLimit()) {
			if (parameters.isCollectd()) {
				System.out.println("PUTVAL \"" + hostname + "/polopoly/committime\" interval="
						+ (parameters.getSleep() / 1000) + " " + System.currentTimeMillis() + ":" + commitTime);
			} else if (parameters.isTimeStamp()) {
				System.out.println(DATE_FORMAT.format(new Date()) + " " + commitTime);
			} else {
				System.out.println(commitTime);
			}
		}
	}

	private String getExternalId() {
		return "pcmd.commit.monitor";
	}

	@Override
	public MonitorParameters createParameters() {
		return new MonitorParameters();
	}

	@Override
	public String getHelp() {
		return "Performs a commit and print the time it took.";
	}

	public void jstack() {
		String command = "jstack " + parameters.getJstackPid();

		StringBuffer result = new StringBuffer();

		Process process;
		try {
			process = new ProcessBuilder(addShellPrefix(command)).redirectErrorStream(true).start();
		} catch (IOException e) {
			throw new FatalToolException(e);
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

		String line;

		try {
			while ((line = reader.readLine()) != null) {
				System.out.println(line);

				result.append(line + "\n");
			}
		} catch (IOException e) {
			// ignore
		}

		try {
			process.waitFor();
		} catch (InterruptedException e) {
			// ignore.
		}

		if (process.exitValue() != 0) {
			System.err.println("Executing " + command + " failed with exit code " + process.exitValue() + ".");
		}
	}

	private String[] addShellPrefix(String command) {
		String[] commandArray = new String[3];
		commandArray[0] = "sh";
		commandArray[1] = "-c";
		commandArray[2] = command;

		return commandArray;
	}
}

package com.polopoly.ps.pcmd.tool;

import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.Arguments;
import com.polopoly.ps.pcmd.argument.NotProvidedException;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.ps.pcmd.argument.Parameters;
import com.polopoly.ps.pcmd.parser.BooleanParser;
import com.polopoly.ps.pcmd.parser.IntegerParser;
import com.polopoly.util.client.PolopolyContext;

public class MonitorParameters implements Parameters {
	private int repeat = 1;
	private int sleep = 5000;
	private boolean timestamp;
	private int limit = 500;
	private int jstackPid;
	private boolean onlySlow;
	private boolean collectd;

	@Override
	public void parseParameters(Arguments args, PolopolyContext context) throws ArgumentException {
		try {
			sleep = args.getOption("sleep", new IntegerParser());
		} catch (NotProvidedException e) {
		}

		try {
			repeat = args.getOption("repeat", new IntegerParser());
		} catch (NotProvidedException e) {
		}

		try {
			setLimit(args.getOption("limit", new IntegerParser()));
		} catch (NotProvidedException e) {
		}

		try {
			setJstackPid(args.getOption("jstackpid", new IntegerParser()));
		} catch (NotProvidedException e) {
		}

		collectd = args.getFlag("collectd", false);
		timestamp = args.getFlag("timestamp", repeat > 1);

		setOnlySlow(args.getFlag("onlyslow", false));
	}

	@Override
	public void getHelp(ParameterHelp help) {
		help.addOption("repeat", new IntegerParser(), "Number of times to repeat a commit.");
		help.addOption("sleep", new IntegerParser(), "Number of ms to sleep between commits.");
		help.addOption("timestamp", new BooleanParser(), "Whether to print a time stamp next to the commit time.");
		help.addOption("jstackpid", new IntegerParser(),
				"PID to run jstack agains when the commit takes longer than <limit> ms.");
		help.addOption("limit", new IntegerParser(), "Number of ms to consider a slow commit.");
		help.addOption("onlyslow", new BooleanParser(), "Only show commits that take longer than <limit> ms.");
		help.addOption("collectd", new BooleanParser(), "Print data in collectd format.");
	}

	public boolean isCollectd() {
		return collectd;
	}

	public int getSleep() {
		return sleep;
	}

	public int getRepeat() {
		return repeat;
	}

	public boolean isTimeStamp() {
		return timestamp;
	}

	public void setJstackPid(int jstackPid) {
		this.jstackPid = jstackPid;
	}

	public int getJstackPid() {
		return jstackPid;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getLimit() {
		return limit;
	}

	public void setOnlySlow(boolean onlySlow) {
		this.onlySlow = onlySlow;
	}

	public boolean isOnlySlow() {
		return onlySlow;
	}
}

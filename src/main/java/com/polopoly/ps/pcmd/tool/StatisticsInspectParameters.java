package com.polopoly.ps.pcmd.tool;

import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.Arguments;
import com.polopoly.ps.pcmd.argument.NotProvidedException;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.ps.pcmd.argument.Parameters;
import com.polopoly.ps.pcmd.parser.IntegerParser;
import com.polopoly.util.client.PolopolyContext;

public class StatisticsInspectParameters implements Parameters {

	private String analyzer;
	private String key;
	private Integer keyCount;

	@Override
	public void getHelp(ParameterHelp help) {
		help.addOption("analyzer", null, "Limit the output to a single, specific analyzer.");
		help.addOption("key", null, "Print the value of a certain key.");
		help.addOption("keycount", null,
				"Number of keys to print for each time bin (if \"key\" is specified this is 1).");
	}

	@Override
	public void parseParameters(Arguments args, PolopolyContext context) throws ArgumentException {
		try {
			analyzer = args.getOptionString("analyzer");
		} catch (NotProvidedException e) {
		}

		try {
			key = args.getOptionString("key");
		} catch (NotProvidedException e) {
		}

		keyCount = args.getOption("keycount", new IntegerParser(), "5");
	}

	/**
	 * @return Returns null to print all analyzers.
	 */
	public String getAnalyzer() {
		return analyzer;
	}

	/**
	 * @return Returns null to not print information on keys.
	 */
	public String getKey() {
		return key;
	}

	public int getKeyCount() {
		return keyCount;
	}
}

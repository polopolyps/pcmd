package com.polopoly.ps.pcmd.argument;

import com.polopoly.ps.pcmd.parser.BooleanParser;
import com.polopoly.util.client.PolopolyContext;

public class InspectParameters  extends ContentIdListParameters {
	
	private boolean escaped;
	private boolean skipNumericalId;
	
	@Override
	public void getHelp(ParameterHelp help) {
		super.getHelp(help);
		help.addOption("escape", new BooleanParser(), "Replace all \":\" by "+ "\"\\:\", useful to create .content format'");
		help.addOption("skipNumericalId", new BooleanParser(), "Hide the numericalid entry in output");
	}
	
	@Override
	public void parseParameters(Arguments args, PolopolyContext context) throws ArgumentException {
		super.parseParameters(args, context);
		escaped = args.getFlag("escape", false);
	    skipNumericalId = args.getFlag("skipNumericalId", false);
	}
	
	public boolean isEscaped() {
		return this.escaped;
	}
	
	public boolean isSkipNumericalId() {
		return skipNumericalId;
	}

}

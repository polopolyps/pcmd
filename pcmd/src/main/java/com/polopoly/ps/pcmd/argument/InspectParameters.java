package com.polopoly.ps.pcmd.argument;

import com.polopoly.ps.pcmd.parser.BooleanParser;
import com.polopoly.util.client.PolopolyContext;

public class InspectParameters  extends ContentIdListParameters {
	
	private boolean escaped;
	
	@Override
	public void getHelp(ParameterHelp help) {
		super.getHelp(help);
		help.addOption("escape", new BooleanParser(), "Replace all \":\" by "+ "\"\\:\", useful to create .content format'");
	}
	
	@Override
	public void parseParameters(Arguments args, PolopolyContext context) throws ArgumentException {
		super.parseParameters(args, context);
		escaped = args.getFlag("escape", false);
	}
	
	public boolean isEscaped() {
		return this.escaped;
	}

}

package com.polopoly.ps.pcmd.argument;

import com.polopoly.util.client.PolopolyContext;

public class EmptyParameters implements Parameters {

	public void parseParameters(Arguments args, PolopolyContext context)
			throws ArgumentException {
		// nothing to parse
	}

	public void getHelp(ParameterHelp help) {
		// no help to get
	}

}

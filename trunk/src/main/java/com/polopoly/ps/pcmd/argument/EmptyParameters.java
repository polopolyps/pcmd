package com.polopoly.ps.pcmd.argument;

import com.polopoly.util.client.PolopolyContext;

public class EmptyParameters implements Parameters {

	@Override
	public void parseParameters(Arguments args, PolopolyContext context)
			throws ArgumentException {
		// nothing to parse
	}

	@Override
	public void getHelp(ParameterHelp help) {
		// no help to get
	}

}

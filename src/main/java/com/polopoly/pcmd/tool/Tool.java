package com.polopoly.pcmd.tool;

import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.ps.pcmd.argument.Parameters;
import com.polopoly.util.client.PolopolyContext;

/**
 * Note that this interface may not be moved as it will break the service
 * declarations in project code.
 */
public interface Tool<P extends Parameters> {
	void execute(PolopolyContext context, P parameters)
			throws FatalToolException;

	P createParameters();

	String getHelp();
}

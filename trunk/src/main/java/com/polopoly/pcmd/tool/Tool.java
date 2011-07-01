package com.polopoly.pcmd.tool;

import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.ps.pcmd.argument.Parameters;
import com.polopoly.util.client.PolopolyContext;
/**
 * Please do not move this interface, otherwise HelpTool wont work
 * @author andrew
 *
 * @param <P>
 */
public interface Tool<P extends Parameters> {
    void execute(PolopolyContext context, P parameters) throws FatalToolException;

    P createParameters();

    String getHelp();
}

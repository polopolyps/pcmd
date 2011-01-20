package com.polopoly.pcmd.tool;

import com.polopoly.pcmd.FatalToolException;
import com.polopoly.pcmd.argument.Parameters;
import com.polopoly.util.client.PolopolyContext;

public interface Tool<P extends Parameters> {
    void execute(PolopolyContext context, P parameters) throws FatalToolException;

    P createParameters();

    String getHelp();
}

package com.polopoly.pcmd.tool;

import com.polopoly.pcmd.argument.Parameters;

public interface Tool<P extends Parameters> {
    void execute(PolopolyContext context, P parameters);

    P createParameters();

    String getHelp();
}

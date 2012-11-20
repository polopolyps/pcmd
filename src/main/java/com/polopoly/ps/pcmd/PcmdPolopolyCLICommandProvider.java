package com.polopoly.ps.pcmd;

import java.util.Collections;
import java.util.Map;

import com.polopoly.cli.command.PolopolyCLICommand;
import com.polopoly.cli.command.PolopolyCLICommandProvider;

public class PcmdPolopolyCLICommandProvider implements PolopolyCLICommandProvider
{
    public Map<String, PolopolyCLICommand> getPolopolyCLICommands()
    {
        return Collections.<String, PolopolyCLICommand>singletonMap("pcmd", new PcmdPolopolyCLICommand());
    }
}

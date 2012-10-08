package com.polopoly.ps.pcmd;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Options;

import com.polopoly.cli.command.PolopolyCLICommand;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.CommandLineArgumentParser;
import com.polopoly.ps.pcmd.argument.DefaultArguments;

public class PcmdPolopolyCLICommand implements PolopolyCLICommand
{
    private DefaultArguments args;

    @Override
    public void execute()
    {
        new Main().execute(args);
    }

    @Override
    public String getDescription()
    {
        return "A PCMD bridge for polopoly-cli";
    }

    @Override
    public Options buildOptions()
    {
        return new Options();
    }

    @Override
    public String getShortUsage()
    {
        return "[OPTIONS]";
    }

    @Override
    public void parseCommandLine(CommandLine arg0) throws MissingArgumentException
    {
        try
        {
            args = new CommandLineArgumentParser().parse(arg0.getArgs());
        } catch (ArgumentException e)
        {
            throw new RuntimeException(e);
        }
    }
}

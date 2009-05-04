package com.polopoly.pcmd;

import java.net.ConnectException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.pcmd.argument.ArgumentException;
import com.polopoly.pcmd.argument.Arguments;
import com.polopoly.pcmd.argument.CommandLineArguments;
import com.polopoly.pcmd.argument.NotProvidedException;
import com.polopoly.pcmd.argument.Parameters;
import com.polopoly.pcmd.tool.HelpParameters;
import com.polopoly.pcmd.tool.HelpTool;
import com.polopoly.pcmd.tool.Tool;
import com.polopoly.pcmd.util.ToolRetriever;
import com.polopoly.pcmd.util.ToolRetriever.NoSuchToolException;
import com.polopoly.util.client.ClientFromArgumentsConfigurator;
import com.polopoly.util.client.PolopolyClient;
import com.polopoly.util.client.PolopolyContext;

public class Main {
    public static void main(String[] args) {
        Logger.getLogger("").setLevel(Level.WARNING);

        CommandLineArguments arguments = null;
        try {
            arguments = new CommandLineArguments(args);
        } catch (ArgumentException e) {
            System.err.println("Invalid parameters: " + e);
            System.exit(1);
        }

        String toolName = null;

        try {
            toolName = arguments.getToolName();
        } catch (NotProvidedException e1) {
            HelpTool tool = new HelpTool();
            HelpParameters parameters = tool.createParameters();

            try {
                parameters.parseParameters(arguments, null);
            } catch (ArgumentException e) {
                e.printStackTrace(System.err);
            }

            tool.execute(null, new HelpParameters());

            System.exit(1);
        }

        PolopolyClient client = new PolopolyClient();
        client.setAttachStatisticsService(false);

        try {
            new ClientFromArgumentsConfigurator(client, arguments).configure();
            PolopolyContext context = client.connect();
            arguments.setContext(context);

            Tool<?> tool = ToolRetriever.getTool(toolName);

            try {
                execute(tool, context, arguments);
            }
            catch (CMRuntimeException e) {
                if (e.getCause() instanceof Exception) {
                    throw (Exception) e.getCause();
                }

                e.printStackTrace(System.err);
                System.exit(1);
            }
        } catch (NoSuchToolException e) {
            System.err.println(e.getMessage());
            System.err.println("Call with \"help\" as argument to see a list of tools.");
        } catch (ArgumentException e) {
            System.err.println("Invalid parameters: " + e.getMessage());
            System.exit(1);
        } catch (ConnectException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }

        System.exit(0);
    }

    private static <T extends Parameters> void execute(Tool<T> tool, PolopolyContext context, Arguments arguments) throws ArgumentException {
        T parameters = tool.createParameters();

        parameters.parseParameters(arguments, context);

        tool.execute(context, parameters);
    }

}

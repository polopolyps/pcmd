package com.polopoly.ps.pcmd;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.Arguments;
import com.polopoly.ps.pcmd.argument.CommandLineArgumentParser;
import com.polopoly.ps.pcmd.argument.DefaultArguments;
import com.polopoly.ps.pcmd.argument.NotProvidedException;
import com.polopoly.ps.pcmd.argument.Parameters;
import com.polopoly.ps.pcmd.tool.HelpParameters;
import com.polopoly.ps.pcmd.tool.HelpTool;
import com.polopoly.ps.pcmd.tool.RequiresIndexServer;
import com.polopoly.ps.pcmd.tool.RequiresSolr;
import com.polopoly.ps.pcmd.util.ToolRetriever;
import com.polopoly.ps.pcmd.util.ToolRetriever.NoSuchToolException;
import com.polopoly.util.client.ClientFromArgumentsConfigurator;
import com.polopoly.util.client.ConnectException;
import com.polopoly.util.client.PolopolyContext;

public class Main {
    public static void main(String[] args) {
        Logger.getLogger("").setLevel(Level.WARNING);

        DefaultArguments arguments = null;
        try {
            arguments = new CommandLineArgumentParser().parse(args);
        } catch (ArgumentException e) {
            System.err.println("Invalid parameters: " + e);
            System.exit(1);
        }

        String toolName = null;

        try {
            toolName = arguments.getToolName();
        } catch (NotProvidedException e1) {
            printToolList(arguments);
        }

        PolopolyContext context = null;

        try {
            Tool<?> tool = ToolRetriever.getTool(toolName);

            PcmdPolopolyClient client = new PcmdPolopolyClient();
            client.setAttachStatisticsService(false);
            client.setAttachSearchService(tool instanceof RequiresIndexServer);
            client.setAttachSolrSearchClient(tool instanceof RequiresSolr);

            new ClientFromArgumentsConfigurator(client, arguments).configure();
            context = client.connect();
            arguments.setContext(context);

            try {
                execute(tool, context, arguments);
            } catch (FatalToolException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            } catch (CMRuntimeException e) {
                if (e.getCause() instanceof Exception) {
                    throw (Exception) e.getCause();
                }

                e.printStackTrace(System.err);
                System.exit(1);
            }
        } catch (NoSuchToolException e) {
            System.err.println(e.getMessage());
            System.err
                    .println("Call with \"help\" as argument to see a list of tools.");
        } catch (ArgumentException e) {
            System.err.println("Invalid parameters: " + e.getMessage());

            if (toolName != null) {
                HelpParameters helpParameters = new HelpParameters();
                helpParameters.setTool(toolName);
                new HelpTool().execute(context, helpParameters);
            }
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

    private static void printToolList(Arguments arguments) {
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

    public static <T extends Parameters> void execute(Tool<T> tool,
            PolopolyContext context, Arguments arguments)
            throws ArgumentException, FatalToolException {
        T parameters = tool.createParameters();

        parameters.parseParameters(arguments, context);

        Set<String> unusedParameters = arguments.getUnusedParameters();

        if (!unusedParameters.isEmpty()) {
            throw new ArgumentException(
                    "The following specified parameters were not recognized: "
                            + unusedParameters);
        }

        tool.execute(context, parameters);
    }

}

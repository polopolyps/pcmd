package com.polopoly.ps.pcmd.tool;

import java.util.ServiceLoader;

import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.ps.pcmd.util.ToolRetriever;
import com.polopoly.ps.pcmd.util.ToolRetriever.NoSuchToolException;
import com.polopoly.util.client.ClientFromArgumentsConfigurator;
import com.polopoly.util.client.PolopolyContext;

public class HelpTool implements Tool<HelpParameters> {
    public HelpParameters createParameters() {
        return new HelpParameters();
    }

    @SuppressWarnings("unchecked")
    public void execute(PolopolyContext context, HelpParameters parameters) {
        if (parameters.getTool() != null) {
            try {
                Tool<?> tool = ToolRetriever.getTool(parameters.getTool());

                ParameterHelp help = new ParameterHelp();

                ClientFromArgumentsConfigurator.getHelp(help);

                tool.createParameters().getHelp(help);

                System.err
                        .println(parameters.getTool() + ": " + tool.getHelp());
                help.print(System.err);
            } catch (NoSuchToolException e) {
                System.err.println(e.getMessage());
                System.err.println("Use Main help to see a list of all tools.");
            }
        } else {
            System.err.println("Usage: pcmd <tool> [--option=value]*");
            System.err
                    .println("Use pcmd help <tool> to get help on a specific tool");

            try {
                System.err.println("Available tools: ");
                ServiceLoader<Tool> toolLoader = ServiceLoader.load(Tool.class);

                for (Tool tool : toolLoader) {
                    StringBuffer sb = new StringBuffer(80);

                    sb.append(ToolRetriever.getToolName(tool.getClass()));

                    while (sb.length() < 20) {
                        sb.append(' ');
                    }

                    sb.append(tool.getHelp());
                    System.err.println(sb);
                }
            } catch (NoClassDefFoundError e) {
                System.err
                        .println("You need JDK 1.6+ to retrieve information on available tools.");
            }
        }
    }

    public String getHelp() {
        return "Returns help on a tool";
    }
}

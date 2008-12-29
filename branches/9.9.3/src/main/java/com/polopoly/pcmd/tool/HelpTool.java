package com.polopoly.pcmd.tool;

import java.util.ServiceLoader;

import com.polopoly.pcmd.Main;
import com.polopoly.pcmd.argument.ParameterHelp;
import com.polopoly.pcmd.util.ToolRetriever;
import com.polopoly.pcmd.util.ToolRetriever.NoSuchToolException;

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
                help.addOption(Main.SERVER, null, "The server name or the connection URL to use to connect to Polopoly. Defaults to localhost.");
                help.addOption(Main.USER, null, "The Polopoly user to log in. Defaults to \"sysadmin\".");
                help.addOption(Main.PASSWORD, null, "The password of the Polopoly user to log in. " +
                		"If not specified, no user will be logged in (which is fine for most operations).");

                tool.createParameters().getHelp(help);
                help.print(System.err);
            } catch (NoSuchToolException e) {
                System.err.println(e.getMessage());
                System.err.println("Use Main help to see a list of all tools.");
            }
        }
        else {
            System.err.println("Usage: pcmd <tool> [--option=value]*");
            System.err.println("Use pcmd help <tool> to get help on a specific tool");

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
            }
            catch (NoClassDefFoundError e) {
                System.err.println("You need JDK 1.6+ to retrieve information on available tools.");
            }
        }
    }

    public String getHelp() {
        return "Returns help on a tool";
    }
}


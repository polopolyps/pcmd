package com.polopoly.ps.pcmd.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.ps.pcmd.util.ToolRetriever;
import com.polopoly.ps.pcmd.util.ToolRetriever.NoSuchToolException;
import com.polopoly.util.client.ClientFromArgumentsConfigurator;
import com.polopoly.util.client.PolopolyContext;

public class HelpTool implements Tool<HelpParameters> {

	@Override
    public HelpParameters createParameters() {
		return new HelpParameters();
	}

	@Override
    @SuppressWarnings({ "rawtypes" })
	public void execute(PolopolyContext context, HelpParameters parameters) {
		if (parameters.getTool() != null) {
			try {
				Tool<?> tool = ToolRetriever.getTool(parameters.getTool());

				ParameterHelp help = new ParameterHelp();

                if (!(tool instanceof DoesNotRequireRunningPolopoly)) {
                    ClientFromArgumentsConfigurator.getHelp(help);
                }

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

				List<Tool> tools = new ArrayList<Tool>();

				for (Tool tool : toolLoader) {
					tools.add(tool);
				}

				Collections.sort(tools, new Comparator<Tool>() {
					@Override
					public int compare(Tool t1, Tool t2) {
						return t1.getClass().getName()
								.compareTo(t2.getClass().getName());
					}
				});

				for (Tool tool : tools) {
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

	@Override
    public String getHelp() {
		return "Returns help on a tool";
	}
}

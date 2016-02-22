package com.polopoly.ps.pcmd.tool;

import java.util.List;

import com.polopoly.common.ProductVersion;
import com.polopoly.pcmd.tool.Tool;
import com.polopoly.pcmd.tool.version.PcmdVersion;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.ps.pcmd.util.ToolRetriever;
import com.polopoly.ps.pcmd.util.ToolRetriever.NoSuchToolException;
import com.polopoly.util.client.ClientFromArgumentsConfigurator;
import com.polopoly.util.client.PolopolyContext;

public class HelpTool implements Tool<HelpParameters>, DoesNotRequireRunningPolopoly {

	public static final String BOLD_END = "\033[0m";
	public static final String BOLD_START = "\033[0;1m";

	@Override
	public HelpParameters createParameters() {
		return new HelpParameters();
	}

	public void execute(PolopolyContext context, HelpParameters parameters) {
		if (parameters.getTool() != null) {
			try {
				Tool<?> tool = ToolRetriever.getTool(parameters.getTool());

				ParameterHelp help = new ParameterHelp();

				if (!(tool instanceof DoesNotRequireRunningPolopoly)) {
					ClientFromArgumentsConfigurator.getHelp(help);
				}

				tool.createParameters().getHelp(help);
				System.err.println("");
				System.err.println(BOLD_START+"   PCMD TOOL NAME" + BOLD_END);
				System.err.println("");
				System.err.println("     " + parameters.getTool());
				System.err.println("");
				System.err.println(BOLD_START+ "   DESCRIPTION" + BOLD_END);
				System.err.println("");
				System.err.println("     " + tool.getHelp());
				System.err.println("");
				
				help.print(System.err);
			} catch (NoSuchToolException e) {
				System.err.println(e.getMessage());
				System.err.println("Use Main help to see a list of all tools.");
			}
		} else {
	    	
			System.err.println("\n Polopoly PS ("+PcmdVersion.CODE + ") pcmd tool for Polopoly " + ProductVersion.CODE_STRING + "\n");
			System.err.println("   Documentation: https://github.com/polopolyps/pcmd");
			System.err.println("   Usage: pcmd <tool> [--option=value ...] [argument ...]");
			System.err.println("   Use pcmd help <tool> to get help on a specific tool\n");
			
			
			

			System.err.println("   Available tools:\n");

			List<Tool<?>> tools = ToolRetriever.getAllTools(true);

			for (Tool<?> tool : tools) {
				StringBuffer sb = new StringBuffer(80);

				sb.append(ToolRetriever.getToolName(tool.getClass()));

				while (sb.length() < 20) {
					sb.append(' ');
				}

				sb.append(tool.getHelp());
				System.err.println("   " + sb);
			}
			
			System.err.println("");
			System.err.println("   " + BOLD_START + "NOTE" + BOLD_END);
			System.err.println("");
			System.err.println("   Pcmd is not covered by the Polopoly support agreement.");
			System.err.println("");
		}
	}

	@Override
	public String getHelp() {
		return "Returns help on a tool";
	}
}

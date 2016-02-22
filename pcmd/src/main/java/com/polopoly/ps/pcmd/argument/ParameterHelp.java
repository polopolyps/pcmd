package com.polopoly.ps.pcmd.argument;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import com.polopoly.ps.pcmd.parser.Parser;
import com.polopoly.ps.pcmd.tool.HelpTool;

public class ParameterHelp {
    private Collection<OptionHelp> arguments = new ArrayList<OptionHelp>();
    private Collection<OptionHelp> options = new ArrayList<OptionHelp>();

    private class OptionHelp {
        private String option;
        private Parser<?> parser;
        private String help;

        private OptionHelp(String option, Parser<?> parser, String help) {
            this.option = option;
            this.parser = parser;
            this.help = help;
        }

        public void print(PrintStream stream) {
            StringBuffer result = new StringBuffer(100);

            result.append("     ");
            result.append(option);

            while (result.length() < 20) {
                result.append(" ");
            }
            
            if (parser != null) {
            	if(!parser.getHelp().startsWith("<")) {
            	result.append("<");
            	}
                result.append(parser.getHelp());
                
                if(!parser.getHelp().endsWith(">")) {
                	result.append(">");
                	}
                
            }

            while (result.length() < 35) {
                result.append(' ');
            }

            result.append(help);
            result.append(" ");
            stream.println(result.toString());
        }
    }

    public void addOption(String option, Parser<?> parser, String help) {
        options.add(new OptionHelp("--" + option, parser, help));
    }

    public void setArguments(Parser<?> parser, String help) {
        arguments.add(new OptionHelp("argument " + (arguments.size()+1), parser, help));
    }

    public void print(PrintStream stream) {
    	
    	if(!arguments.isEmpty()) {
    		System.err.println("");
    		System.err.println("   " + HelpTool.BOLD_START + "ARGUMENTS" + HelpTool.BOLD_END);
    		System.err.println("");
    	}
    	
        for (OptionHelp argument : arguments) {
            argument.print(stream);
        
        }
        
        if(!options.isEmpty()) {
        System.err.println("");
        System.err.println("   " + HelpTool.BOLD_START + "OPTIONS" + HelpTool.BOLD_END);
        System.err.println("");
        }
        for (OptionHelp option : options) {
            option.print(stream);
        }
        
        System.err.println("");
        
    }
}

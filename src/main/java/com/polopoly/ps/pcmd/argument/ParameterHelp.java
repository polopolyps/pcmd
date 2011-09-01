package com.polopoly.ps.pcmd.argument;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import com.polopoly.ps.pcmd.parser.Parser;

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

            result.append(option);

            while (result.length() < 25) {
                result.append(' ');
            }

            result.append(' ');

            if (parser != null) {
                result.append(parser.getHelp());
            }

            result.append("\n");

            for (int i = 0; i < 10; i++) {
                result.append(' ');
            }

            result.append(help);

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
        for (OptionHelp argument : arguments) {
            argument.print(stream);
        }

        for (OptionHelp option : options) {
            option.print(stream);
        }
    }
}

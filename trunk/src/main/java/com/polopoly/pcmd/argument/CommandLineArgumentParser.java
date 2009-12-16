package com.polopoly.pcmd.argument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandLineArgumentParser {
    public DefaultArguments parse(String[] args) throws ArgumentException {
        Map<String, List<String>> options = new HashMap<String, List<String>>();
        List<String> arguments = new ArrayList<String>();
        String toolName = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.startsWith("--")) {
               String option = arg.substring(2);

               if (option.equals("")) {
                   throw new ArgumentException("Expected option name after \"--\".");
               }

               int j = option.indexOf("=");
               String optionValue;
               String optionName;

               if (j == -1) {
                   optionName = option;
                   optionValue = "true";
               }
               else {
                   optionName = option.substring(0, j);
                   optionValue = option.substring(j+1);
               }

               List<String> existingValues = options.get(optionName);

               if (existingValues == null) {
                   existingValues = new ArrayList<String>();
                   options.put(optionName, existingValues);
               }

               existingValues.add(optionValue);
            }
            else {
                if (arguments.size() == 0 && toolName == null) {
                    toolName = arg;
                }
                else {
                    arguments.add(arg);
                }
            }
        }

        return new DefaultArguments(toolName, options, arguments);
    }
}

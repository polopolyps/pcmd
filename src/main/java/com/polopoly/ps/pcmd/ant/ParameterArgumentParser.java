package com.polopoly.ps.pcmd.ant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.types.Parameter;

import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.DefaultArguments;

public class ParameterArgumentParser {

    public DefaultArguments parse(String toolName, List<Parameter> parameters) throws ArgumentException {
        Map<String, List<String>> options = new HashMap<String, List<String>>();
        List<String> arguments = new ArrayList<String>();

        for (Parameter parameter : parameters) {
            String value = parameter.getValue();
            String name = parameter.getName();

            if (name != null) {
                addOption(options, name, value);
            }
            else if (value != null) {
                if (value.startsWith("--")) {
                    int i = value.indexOf("=");

                    String optionName;

                    if (i == -1) {
                        i = value.indexOf("-", 2);
                    }

                    if (i == -1) {
                        optionName = value.substring(2);

                        addOption(options, optionName, "true");
                    }
                    else {
                        optionName = value.substring(2, i);
                        String optionValue = value.substring(i+1);

                        addOption(options, optionName, optionValue);
                    }
                }
                // for the batch script on windows we need to be able to pass empty string to
                // signal "no more parameters" since we can't easily support a variable number
                // of paramaters.
                else if (!value.equals("")) {
                    arguments.add(value);
                }
            }
            else {
                throw new ArgumentException("A parameter with neither name nor value was specified.");
            }
        }

        return new DefaultArguments(toolName, options, arguments);
    }

    private void addOption(Map<String, List<String>> options,
            String optionName, String optionValue) {
        List<String> optionValues = options.get(optionName);

        if (optionValues == null) {
            optionValues = new ArrayList<String>();
            options.put(optionName, optionValues);
        }

        optionValues.add(optionValue);
    }

}

package com.polopoly.pcmd.argument;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.pcmd.parser.BooleanParser;
import com.polopoly.pcmd.parser.ContentIdParser;
import com.polopoly.pcmd.parser.ParseException;
import com.polopoly.pcmd.parser.Parser;
import com.polopoly.pcmd.tool.PolopolyContext;
import com.polopoly.pcmd.util.FetchingIterator;

public class CommandLineArguments implements Arguments {
    private Map<String, String> options = new HashMap<String, String>();
    private List<String> arguments = new ArrayList<String>();
    private PolopolyContext context;

    public CommandLineArguments(String[] args, PolopolyContext context) throws ArgumentException {
        this(args);

        setContext(context);
    }

    public CommandLineArguments(String[] args) throws ArgumentException {
        // argument 0 is the tool name
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];

            if (arg.startsWith("--")) {
               String option = arg.substring(2);

               if (option.equals("")) {
                   throw new ArgumentException("Expected option name after \"--\".");
               }

               int j = option.indexOf("=");

               if (j == -1) {
                   options.put(option, "true");
               }
               else {
                   options.put(option.substring(0, j), option.substring(j+1));
               }
            }
            else {
                arguments.add(arg);
            }
        }
    }

    public void setContext(PolopolyContext context) {
        this.context = context;
    }

    public boolean getFlag(String option, boolean defaultValue) throws ParseException {
        String optionValue = options.get(option);

        if (optionValue == null) {
            return defaultValue;
        }

        return new BooleanParser().parse(optionValue);
    }

    public Iterator<ContentId> getArgumentContentIds() throws ArgumentException {
        if (arguments.size() == 0) {
            throw new NotProvidedException("Expected a list of content IDs as arguments.");
        }

        Collection<ContentId> contentIds = new ArrayList<ContentId>();

        Parser<ContentId> parser;

        if (context != null) {
            parser = new ContentIdParser(context);
        }
        else {
            parser = new ContentIdParser();
        }

        for (String argument : arguments) {
            contentIds.add(parser.parse(argument));
        }

        return contentIds.iterator();
    }

    public <T> T getOption(String name, Parser<T> parser)
            throws ArgumentException {
        try {
            return parser.parse(getOptionString(name));
        }
        catch (ParseException e) {
            e.setField(name);

            throw e;
        }
    }

    public String getOptionString(String name) throws ArgumentException {
        String optionString = options.get(name);

        if (optionString == null) {
            throw new NotProvidedException(name);
        }

        return optionString;
    }

    public String getOptionString(String name, String defaultValue) {
        try {
            return getOptionString(name);
        } catch (ArgumentException e) {
            return defaultValue;
        }
    }

    private Iterator<ContentId> stdInContentIdIterator = new FetchingIterator<ContentId>() {
        private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        private boolean first = true;

        @Override
        protected ContentId fetch() {
            if (first) {
                System.err.println("Reading content IDs from standard input...");
                first = false;
            }

            try {
                String readLine = reader.readLine();

                if (readLine == null) {
                    return null;
                }

                if (readLine.trim().length() == 0) {
                    return fetch();
                }

                return new ContentIdParser(context).parse(readLine);
            } catch (IOException e) {
                System.err.println(e.toString());

                return null;
            } catch (ParseException e) {
                try {
                    if (getFlag(ContentIdListParameters.STOPONEXCEPTION, true)) {
                        throw new CMRuntimeException(e);
                    }
                    else {
                        return fetch();
                    }
                } catch (ParseException e1) {
                    throw new CMRuntimeException(e);
                }
            }
        }
    };

    public Iterator<ContentId> getStdInContentIds() throws ArgumentException {
        return stdInContentIdIterator;
    }

    public String getArgument(int i) {
        return arguments.get(i);
    }

    public int getArgumentCount() {
        return arguments.size();
    }
}

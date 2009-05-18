package com.polopoly.util.client;

import com.polopoly.pcmd.argument.ArgumentException;
import com.polopoly.pcmd.argument.Arguments;
import com.polopoly.pcmd.argument.NotProvidedException;

public class ClientFromArgumentsConfigurator {
    private PolopolyClient client;
    private Arguments arguments;

    public static final String SERVER = "server";
    public static final String USER = "loginuser";
    public static final String PASSWORD = "loginpassword";
    private static final String VERBOSE = "verbose";

    public ClientFromArgumentsConfigurator(PolopolyClient client, Arguments arguments) {
        this.client = client;
        this.arguments = arguments;
    }

    public void configure() throws ArgumentException {
        client.setApplicationName("pcmd");
        client.setConnectionUrl(arguments.getOptionString(SERVER, "localhost"));
        client.setUserName(arguments.getOptionString(USER, "sysadmin"));

        try {
            client.setPassword(arguments.getOptionString(PASSWORD));
        } catch (NotProvidedException e) {
        }

        if (arguments.getFlag(VERBOSE, false)) {
            client.setLogger(new PolopolyClientLogger() {
                public void info(String logMessage) {
                    System.err.println(logMessage);
                }
            });
        }
    }
}

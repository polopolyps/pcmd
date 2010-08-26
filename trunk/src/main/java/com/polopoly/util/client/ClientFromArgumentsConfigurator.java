package com.polopoly.util.client;


import com.polopoly.pcmd.PcmdPolopolyClient;
import com.polopoly.pcmd.argument.ArgumentException;
import com.polopoly.pcmd.argument.Arguments;
import com.polopoly.pcmd.argument.NotProvidedException;
import com.polopoly.pcmd.argument.ParameterHelp;
import com.polopoly.pcmd.parser.CreatingDirectoryParser;
import com.polopoly.pcmd.parser.IntegerParser;

public class ClientFromArgumentsConfigurator {
    private PcmdPolopolyClient client;

    private Arguments arguments;

    public static final String POLICY_CACHE = "policycache";

    public static final String CONTENT_CACHE = "contentcache";

    public static final String SERVER = "server";

    public static final String USER = "loginuser";

    public static final String PASSWORD = "loginpassword";

    private static final String VERBOSE = "verbose";

    private static final String PERSISTENCE_CACHE_DIR = "persistencecachedir";

    public ClientFromArgumentsConfigurator(PcmdPolopolyClient client, Arguments arguments) {
        this.client = client;
        this.arguments = arguments;
    }

    public void configure() throws ArgumentException {
        client.setApplicationName("pcmd");
        client.setConnectionUrl(arguments.getOptionString(SERVER, "localhost"));
        client.setUserName(arguments.getOptionString(USER, "sysadmin"));

        try {
            client.setPolicyCacheSize(arguments.getOption(POLICY_CACHE, new IntegerParser()));
        } catch (NotProvidedException e) {
        }

        try {
            client.setContentCacheSize(arguments.getOption(CONTENT_CACHE, new IntegerParser()));
        } catch (NotProvidedException e) {
        }

        try {
            client.setPassword(arguments.getOptionString(PASSWORD));
        } catch (NotProvidedException e) {
        }

        try {
            String persistenceCacheDir = arguments.getOptionString(PERSISTENCE_CACHE_DIR);
            client.setPersistenceCacheDir((new CreatingDirectoryParser()).parse(persistenceCacheDir));
        }catch (NotProvidedException e) {
        }

        if (arguments.getFlag(VERBOSE, false)) {
            client.setLogger(new PolopolyClientLogger() {
                public void info(String logMessage) {
                    System.err.println(logMessage);
                }
            });
        }
    }

    public static void getHelp(ParameterHelp help) {
        help.addOption(SERVER, null,
                       "The server name or the connection URL to use to connect to Polopoly. Defaults to localhost.");
        help.addOption(USER, null, "The Polopoly user to log in. Defaults to \"sysadmin\".");
        help.addOption(PASSWORD, null, "The password of the Polopoly "
                                       + "user to log in. If not specified, no user will be logged in "
                                       + "(which is fine for most operations).");
        help.addOption(POLICY_CACHE, new IntegerParser(), "The size of the policy cache of the client.");
        help.addOption(CONTENT_CACHE, new IntegerParser(), "The size of the content cache of the client.");
        help.addOption(PERSISTENCE_CACHE_DIR, new CreatingDirectoryParser(), "Enable persistence cache.");
    }
}

package com.polopoly.pcmd;

import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.FinderException;

import com.polopoly.application.ConnectionProperties;
import com.polopoly.application.StandardApplication;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.client.EjbCmClient;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.search.index.RmiSearchClient;
import com.polopoly.management.ManagedBeanRegistry;
import com.polopoly.management.jmx.JMXManagedBeanRegistry;
import com.polopoly.pcmd.argument.ArgumentException;
import com.polopoly.pcmd.argument.Arguments;
import com.polopoly.pcmd.argument.CommandLineArguments;
import com.polopoly.pcmd.argument.NotProvidedException;
import com.polopoly.pcmd.argument.Parameters;
import com.polopoly.pcmd.tool.HelpParameters;
import com.polopoly.pcmd.tool.HelpTool;
import com.polopoly.pcmd.tool.PolopolyContext;
import com.polopoly.pcmd.tool.Tool;
import com.polopoly.pcmd.util.ToolRetriever;
import com.polopoly.pcmd.util.ToolRetriever.NoSuchToolException;
import com.polopoly.statistics.client.StatisticsClient;
import com.polopoly.statistics.message.logging.UDPLogMsgClient;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.User;
import com.polopoly.user.server.UserId;

public class Main {

    public static final String SERVER = "server";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    private static final String VERBOSE = "verbose";

    public static void main(String[] args) {
        Logger.getLogger("").setLevel(Level.WARNING);

        CommandLineArguments arguments = null;
        try {
            arguments = new CommandLineArguments(args);
        } catch (ArgumentException e) {
            System.err.println("Invalid parameters: " + e);
            System.exit(1);
        }

        String toolName = null;

        try {
            toolName = arguments.getToolName();
        } catch (NotProvidedException e1) {
            HelpTool tool = new HelpTool();
            HelpParameters parameters = tool.createParameters();

            try {
                parameters.parseParameters(arguments, null);
            } catch (ArgumentException e) {
                e.printStackTrace(System.err);
            }

            tool.execute(null, new HelpParameters());

            System.exit(1);
        }

        String connectionUrl = arguments.getOptionString(SERVER, "localhost");

        if (connectionUrl.indexOf('/') == -1 && connectionUrl.indexOf(':') == -1) {
            // if the URL does not contain a slash or colon, it's not a URL but just the server name. Assume default URL on it.
            connectionUrl = "http://" + connectionUrl + ":8040/connection.properties";
        }

        CmClient cmClient = null;
        StandardApplication app = null;
        RmiSearchClient searchClient = null;
        StatisticsClient statisticsClient = null;
        UDPLogMsgClient logMsgClient = null;

        try {
            // Create connection properties from an URL.
            ConnectionProperties connectionProperties =
                new ConnectionProperties(new URL(connectionUrl));

            // Create a ManagedBeanRegistry from the standard MBeanServer.
            ManagedBeanRegistry registry =
                new JMXManagedBeanRegistry(ManagementFactory.getPlatformMBeanServer());

            // Create a CM client ApplicationComponent.
            cmClient = new EjbCmClient();

            // Create the Application.
            app = new StandardApplication("pcmd");

            // Set the registry.
            app.setManagedBeanRegistry(registry);

            // Add the CM client.
            app.addApplicationComponent(cmClient);

            searchClient = new RmiSearchClient();
            app.addApplicationComponent(searchClient);

            statisticsClient = new StatisticsClient();
            app.addApplicationComponent(statisticsClient);

            logMsgClient = new UDPLogMsgClient();
            app.addApplicationComponent(logMsgClient);

            // Read connection properties.
            app.readConnectionProperties(connectionProperties);

            // Init.
            app.init();
        } catch (Exception e) {
            System.err.println("Error connecting to Polopoly server with connection URL " + connectionUrl + ": " + e);
            System.exit(1);
        }

        PolicyCMServer server = cmClient.getPolicyCMServer();

        PolopolyContext context = new PolopolyContext(cmClient, searchClient);
        arguments.setContext(context);

        try {
            String userName = arguments.getOptionString(USER, "sysadmin");

            try {
                String password = arguments.getOptionString(PASSWORD);

                Caller caller = context.getUserServer().loginAndMerge(userName, password, server.getCurrentCaller());

                server.setCurrentCaller(caller);

                if (arguments.getFlag(VERBOSE, false)) {
                    System.err.println("Logged in user \"" + userName + "\".");
                }
            } catch (NotProvidedException e) {
                UserId userId;

                try {
                    User user = context.getUserServer().getUserByLoginName(userName);
                    userId = user.getUserId();
                    server.setCurrentCaller(new Caller(userId, null, null));

                    if (arguments.getFlag(VERBOSE, false)) {
                        System.err.println("No password provided. Set caller to user \"" + userName + "\" but did not log in.");
                    }
                } catch (FinderException e2) {
                    System.err.println("The specified user " + userName + " could not be found.");
                    System.exit(1);
                }
            } catch (FinderException e) {
                System.err.println("The specified user " + userName + " could not be found.");
                System.exit(1);
            }

            Tool<?> tool = ToolRetriever.getTool(toolName);

            try {
                execute(tool, context, arguments);
            }
            catch (CMRuntimeException e) {
                if (e.getCause() instanceof Exception) {
                    throw (Exception) e.getCause();
                }

                e.printStackTrace(System.err);
                System.exit(1);
            }
        } catch (NoSuchToolException e) {
            System.err.println(e.getMessage());
            System.err.println("Call with \"help\" as argument to see a list of tools.");
        } catch (ArgumentException e) {
            System.err.println("Invalid parameters: " + e.getMessage());

            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }

        System.exit(0);
    }

    private static <T extends Parameters> void execute(Tool<T> tool, PolopolyContext context, Arguments arguments) throws ArgumentException {
        T parameters = tool.createParameters();

        parameters.parseParameters(arguments, context);

        tool.execute(context, parameters);
    }

}

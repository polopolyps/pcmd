package com.polopoly.util.client;

import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.FinderException;

import com.polopoly.application.ConnectionProperties;
import com.polopoly.application.StandardApplication;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.client.EjbCmClient;
import com.polopoly.cm.search.index.RmiSearchClient;
import com.polopoly.community.client.CommunityClient;
import com.polopoly.management.ManagedBeanRegistry;
import com.polopoly.management.jmx.JMXManagedBeanRegistry;
import com.polopoly.statistics.client.StatisticsClient;
import com.polopoly.statistics.message.logging.UDPLogMsgClient;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.User;
import com.polopoly.user.server.UserId;

public class PolopolyClient {
    private String applicationName = "polopolyclient";
    private String connectionUrl = "localhost";
    private String userName = "sysadmin";
    private String password = null;

    private static final Logger javaUtilLogger = Logger.getLogger(PolopolyClient.class.getName());
    private PolopolyClientLogger logger =
        new PolopolyClientLogger() {
            public void info(String logMessage) {
                javaUtilLogger.log(Level.INFO, logMessage);
            }};


    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setLogger(PolopolyClientLogger logger) {
        this.logger = logger;
    }

    public PolopolyClientLogger getLogger() {
        return logger;
    }

    public PolopolyContext connect() {
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
            app = new StandardApplication(applicationName);

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

            try {
                CommunityClient communityClient = new CommunityClient(cmClient);
                app.addApplicationComponent(communityClient);
            } catch (Throwable t) {
                // Community JAR not present in class path. Skip it.
            }

            // Read connection properties.
            app.readConnectionProperties(connectionProperties);

            // Init.
            app.init();
        } catch (Exception e) {
            System.err.println("Error connecting to Polopoly server with connection URL " + connectionUrl + ": " + e);
            System.exit(1);
        }

        PolopolyContext context = new PolopolyContext(cmClient, searchClient);

        login(context);

        return context;
    }

    private void login(PolopolyContext context) {
        try {
            if (password != null) {
                Caller caller = context.getUserServer().loginAndMerge(userName, password,
                        context.getPolicyCMServer().getCurrentCaller());

                context.getPolicyCMServer().setCurrentCaller(caller);

                logger.info("Logged in user \"" + userName + "\".");
            }
            else {
                UserId userId;

                try {
                    User user = context.getUserServer().getUserByLoginName(userName);
                    userId = user.getUserId();
                    context.getPolicyCMServer().setCurrentCaller(new Caller(userId, null, null));

                    logger.info("No password provided. Set caller to user \"" + userName + "\" but did not log in.");
                } catch (FinderException e2) {
                    System.err.println("The specified user " + userName + " could not be found.");
                    System.exit(1);
                }
            }
        } catch (FinderException e) {
            System.err.println("The specified user " + userName + " could not be found.");
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

}

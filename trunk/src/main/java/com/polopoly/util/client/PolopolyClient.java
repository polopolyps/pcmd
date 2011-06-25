package com.polopoly.util.client;

import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.FinderException;

import com.polopoly.application.ConnectionProperties;
import com.polopoly.application.StandardApplication;
import com.polopoly.cache.LRUSynchronizedUpdateCache;
import com.polopoly.cm.client.CMServer;
import com.polopoly.cm.client.ContentFilterSettings;
import com.polopoly.cm.client.EjbCmClient;
import com.polopoly.cm.client.filter.ContentFilter;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.search.index.RmiSearchClient;
import com.polopoly.community.client.CommunityClient;
import com.polopoly.management.ManagedBeanRegistry;
import com.polopoly.management.jmx.JMXManagedBeanRegistry;
import com.polopoly.poll.client.PollClient;
import com.polopoly.search.solr.SolrIndexName;
import com.polopoly.search.solr.SolrSearchClient;
import com.polopoly.statistics.client.StatisticsClient;
import com.polopoly.statistics.message.logging.UDPLogMsgClient;
import com.polopoly.user.server.AuthenticationFailureException;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.User;
import com.polopoly.user.server.UserId;

public class PolopolyClient {
	private String applicationName = "polopolyclient";

	private String connectionUrl = "localhost";

	private String userName = "sysadmin";

	private String password = null;

	private boolean attachSearchService = true;

	private boolean attachStatisticsService = true;
	
	private boolean attachSolrSearchClient = true;

	private boolean attachPollService = false;

	private boolean attachLRUSynchronizedUpdateCache = false;

	private List<Class<? extends ContentFilter>> contentFilterClasses = new ArrayList<Class<? extends ContentFilter>>();

	private static final Logger javaUtilLogger = Logger
			.getLogger(PolopolyClient.class.getName());

	private PolopolyClientLogger logger = new PolopolyClientLogger() {
		public void info(String logMessage) {
			javaUtilLogger.log(Level.INFO, logMessage);
		}
	};

	public boolean isAttachSolrSearchClient() {
        return attachSolrSearchClient;
    }

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

	public PolopolyContext connect() throws ConnectException {
		if (connectionUrl.indexOf('/') == -1
				&& connectionUrl.indexOf(':') == -1) {
			// if the URL does not contain a slash or colon, it's not a URL but
			// just the server name. Assume default URL on it.
			connectionUrl = "http://" + connectionUrl
					+ ":8040/connection.properties";
		}

		EjbCmClient cmClient = null;
		RmiSearchClient searchClient = null;
		StatisticsClient statisticsClient = null;
		UDPLogMsgClient logMsgClient = null;
		SolrSearchClient solrSearchClient = null;
		PollClient pollClient = null;

		try {
			// Create connection properties from an URL.
			ConnectionProperties connectionProperties = new ConnectionProperties(
					new URL(connectionUrl));

			// Create a ManagedBeanRegistry from the standard MBeanServer.
			ManagedBeanRegistry registry = new JMXManagedBeanRegistry(
					ManagementFactory.getPlatformMBeanServer());

			// Create the Application.
			final StandardApplication app = new StandardApplication(
					applicationName);

			// Create a CM client ApplicationComponent.

			cmClient = new EjbCmClient() {
				@Override
				protected PolicyCMServer createPolicyCMServer(
						CMServer legacyWrapper) {
					return PolopolyClient.this.createPolicyCMServer(
							super.createPolicyCMServer(legacyWrapper), this,
							app, legacyWrapper);
				}
			};

			setUpCmClient(cmClient);

			if (contentFilterClasses.size() > 0) {
				ContentFilterSettings contentFilterSettings = new ContentFilterSettings();

				contentFilterSettings
						.setContentFilterClassNames(toNames(contentFilterClasses));

				cmClient.setContentFilterSettings(contentFilterSettings);
			}

			// Set the registry.
			app.setManagedBeanRegistry(registry);

			// Add the CM client.
			app.addApplicationComponent(cmClient);

			if (isAttachSearchService()) {
				searchClient = new RmiSearchClient();
				app.addApplicationComponent(searchClient);
			}
			
			if (isAttachSolrSearchClient()) {
			    solrSearchClient = new SolrSearchClient(cmClient);
			    solrSearchClient.setIndexName(new SolrIndexName("public"));
			    app.addApplicationComponent(solrSearchClient);
			}

			if (isAttachPollService()) {
				pollClient = new PollClient();
				app.addApplicationComponent(pollClient);
			}

			if (isAttachStatisticsService()) {
				statisticsClient = new StatisticsClient();
				app.addApplicationComponent(statisticsClient);
			}

			if (isAttachLRUSynchronizedUpdateCache()) {
				LRUSynchronizedUpdateCache cache = new LRUSynchronizedUpdateCache();

				setUpLRUSynchronizedUpdateCache(cache);

				app.addApplicationComponent(cache);
			}

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
			PolopolyContext context = new PolopolyContext(app);

			login(context);

			return context;
		} catch (ConnectException e) {
			throw e;
		} catch (Exception e) {
			throw new ConnectException(
					"Error connecting to Polopoly server with connection URL "
							+ connectionUrl + ": " + e, e);
		}
	}

	/**
	 * Intended for overriding.
	 */
	protected void setUpCmClient(EjbCmClient cmClient) {
	}

	/**
	 * Intended for overriding for clients needing to wrap the CM server.
	 */
	protected PolicyCMServer createPolicyCMServer(
			PolicyCMServer originalServer, EjbCmClient cmClient,
			StandardApplication app, CMServer legacyWrapper) {
		return originalServer;
	}

	private List<String> toNames(List<? extends Class<?>> klasses) {
		List<String> result = new ArrayList<String>();

		for (Class<?> klass : klasses) {
			result.add(klass.getName());
		}

		return result;
	}

	public void addContentFilter(
			Class<? extends ContentFilter> contentFilterClass) {
		contentFilterClasses.add(contentFilterClass);
	}

	private void loginUserWithoutPassword(PolopolyContext context)
			throws Exception {
		User user = context.getUserServer().getUserByLoginName(userName);
		UserId userId = user.getUserId();
		context.getPolicyCMServer().setCurrentCaller(
				new NonLoggedInCaller(userId, null, null, userName));

		logger.info("No password provided. Set caller to user \"" + userName
				+ "\" but did not log in.");
	}

	private void loginUserWithPassword(PolopolyContext context)
			throws Exception {
		Caller caller = context.getUserServer().loginAndMerge(userName,
				password, context.getPolicyCMServer().getCurrentCaller());

		context.getPolicyCMServer().setCurrentCaller(caller);

		logger.info("Logged in user \"" + userName + "\".");
	}

	private void login(PolopolyContext context) throws ConnectException {
		try {
			if (password != null) {
				loginUserWithPassword(context);
			} else {
				loginUserWithoutPassword(context);
			}
		} catch (FinderException e) {
			throw new ConnectException("The user " + userName
					+ " to log in could not be found.");
		} catch (AuthenticationFailureException e) {
			throw new ConnectException("The password supplied for user "
					+ userName + " was incorrect.");
		} catch (Exception e) {
			throw new ConnectException(
					"An error occurred while trying to log in user " + userName
							+ ": " + e.getMessage(), e);
		}
	}

	public void setAttachStatisticsService(boolean attachStatisticsService) {
		this.attachStatisticsService = attachStatisticsService;
	}

	public boolean isAttachStatisticsService() {
		return attachStatisticsService;
	}

	public void setAttachPollService(boolean attachPollService) {
		this.attachPollService = attachPollService;
	}

	public boolean isAttachPollService() {
		return this.attachPollService;
	}

	public void setAttachSearchService(boolean attachSearchService) {
		this.attachSearchService = attachSearchService;
	}

	public boolean isAttachSearchService() {
		return attachSearchService;
	}

	public void setAttachLRUSynchronizedUpdateCache(
			boolean attachLRUSynchronizedUpdateCache) {
		this.attachLRUSynchronizedUpdateCache = attachLRUSynchronizedUpdateCache;
	}

	public boolean isAttachLRUSynchronizedUpdateCache() {
		return attachLRUSynchronizedUpdateCache;
	}

	/**
	 * Intended for overriding.
	 */
	protected void setUpLRUSynchronizedUpdateCache(
			LRUSynchronizedUpdateCache cache) {
	}

    public void setAttachSolrSearchClient(boolean attachSolrSearchClient) {
        this.attachSolrSearchClient = attachSolrSearchClient;
    }
}

package com.polopoly.util.client;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.FinderException;

import com.polopoly.application.ConnectionProperties;
import com.polopoly.application.ConnectionPropertiesConfigurationException;
import com.polopoly.application.IllegalApplicationStateException;
import com.polopoly.application.StandardApplication;
import com.polopoly.cache.LRUSynchronizedUpdateCache;
import com.polopoly.cm.client.CMServer;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.client.CmClientBase;
import com.polopoly.cm.client.CmClientFacade;
import com.polopoly.cm.client.ContentFilterSettings;
import com.polopoly.cm.client.EjbCmClient;
import com.polopoly.cm.client.HttpContentRepositoryClient;
import com.polopoly.cm.client.HttpEnvironment;
import com.polopoly.cm.client.HttpFileServiceClient;
import com.polopoly.cm.client.HttpUserServiceClient;
import com.polopoly.cm.client.UserServiceClient;
import com.polopoly.cm.client.filter.ContentFilter;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.search.index.RmiSearchClient;
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
	private String applicationName = "polopolyPCMDclient";

	private String connectionUrl = "localhost";

	private String userName = "sysadmin";

	private String password = null;

	private boolean attachSearchService = true;

	private boolean attachStatisticsService = true;

	private boolean attachSolrSearchClient = true;

	private boolean attachPollService = false;
	
	private boolean attachHttpFileService = false;

	private boolean attachLRUSynchronizedUpdateCache = false;

	private List<Class<? extends ContentFilter>> contentFilterClasses = new ArrayList<Class<? extends ContentFilter>>();

	// Additional indexes to configure.
	private List<String> additionalIndexes = new LinkedList<String>();

	private static final Logger javaUtilLogger = Logger.getLogger(PolopolyClient.class.getName());

	private static final int SECONDS = 1000;
	private static final int DEFAULT_CONNECTION_TIMEOUT = 30 * SECONDS;

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

	private boolean testConnection(String url) {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
			connection.setReadTimeout(DEFAULT_CONNECTION_TIMEOUT);
			connection.connect();

			if (connection.getResponseCode() >= 400 && connection.getResponseCode() < 600) {
				return false;
			}

			return true;
		} catch (MalformedURLException e) {
			javaUtilLogger.log(Level.WARNING, "URL(" + url + ") is invalid: " + e.getMessage(), e);
		} catch (IOException e) {
			// fine. not available.
		}

		return false;
	}

	public String getConnectionPropertiesUrl(String serverName) throws ConnectException {
		// from 10.3 using mvn p:run
		String mavenJbossUrl = "http://" + serverName + ":8081/connection-properties/connection.properties";

		if (testConnection(mavenJbossUrl)) {
			return mavenJbossUrl;
		}

		// before 10.3 or not started using mvn p:run
		String j2eeContainerUrl = "http://" + serverName + ":8040/connection.properties";

		if (testConnection(j2eeContainerUrl)) {
			return j2eeContainerUrl;
		}

		throw new ConnectException(String.format("Could not get connection properties, both %s and %s are invalid.",
			j2eeContainerUrl, mavenJbossUrl));
	}

	public PolopolyContext connect() throws ConnectException {
		// If connection URL had not been set at all or it had been set to only
		// a host name, deduce the connection properties URL.
		if (connectionUrl.indexOf('/') == -1 && connectionUrl.indexOf(':') == -1) {
			// if the URL does not contain a slash or colon, it's not a URL but
			// just the server name
			connectionUrl = getConnectionPropertiesUrl(connectionUrl);
		}

		for (ConnectListener listener : getConnectListeners()) {
			listener.willConnectToPolopoly(this);
		}

		CmClientBase cmClient = null;
		RmiSearchClient searchClient = null;
		StatisticsClient statisticsClient = null;
		UDPLogMsgClient logMsgClient = null;
		PollClient pollClient = null;
		HttpFileServiceClient httpFileClient = null;

		try {
			ConnectionProperties connectionProperties = new ConnectionProperties(new URL(connectionUrl));

			ManagedBeanRegistry registry = new JMXManagedBeanRegistry(ManagementFactory.getPlatformMBeanServer());

			final StandardApplication app = new StandardApplication(applicationName);

			cmClient = createClient(connectionProperties, app);

			setUpCmClient(cmClient);

			if (contentFilterClasses.size() > 0) {
				ContentFilterSettings contentFilterSettings = new ContentFilterSettings();

				contentFilterSettings.setContentFilterClassNames(toNames(contentFilterClasses));

				cmClient.setContentFilterSettings(contentFilterSettings);
			}

			app.setManagedBeanRegistry(registry);

			app.addApplicationComponent(cmClient);

			if (isAttachSearchService()) {
				searchClient = new RmiSearchClient();
				app.addApplicationComponent(searchClient);
			}

			if (isAttachSolrSearchClient()) {
				createSolrSearchClient(cmClient, app, "public");
				createSolrSearchClient(cmClient, app, "internal");
				for (String index : additionalIndexes) {
					createSolrSearchClient(cmClient, app, index);
				}
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
			
			 if (isAttachHttpFileService()) {
	                httpFileClient = new HttpFileServiceClient();
	                app.addApplicationComponent(httpFileClient);
	            }

			logMsgClient = new UDPLogMsgClient();
			app.addApplicationComponent(logMsgClient);

			// Read connection properties.
			app.readConnectionProperties(connectionProperties);
			
			// Init.
			app.init();
			
			PolopolyContext context = new PolopolyContext(app);

			login(context);

			for (ConnectListener listener : getConnectListeners()) {
				listener.connectedToPolopoly(context);
			}

			return context;
		} catch (ConnectException e) {
			throw e;
		} catch (Exception e) {
			throw new ConnectException("Error connecting to Polopoly server with connection URL " + connectionUrl
										+ ": " + e, e);
		}
	}

	protected CmClientBase createClient(ConnectionProperties connectionProperties, final StandardApplication application) throws IllegalArgumentException, ConnectionPropertiesConfigurationException, IllegalApplicationStateException {
        if (connectionProperties.getBean("cm", "httpEnvironment", HttpEnvironment.class) != null) {
            HttpContentRepositoryClient contentRepositoryClient = new HttpContentRepositoryClient();
            @SuppressWarnings("deprecation")
            UserServiceClient userServiceClient = new HttpUserServiceClient();

            CmClientFacade cmClient = new CmClientFacade() {
				@Override
				protected PolicyCMServer createPolicyCMServer(CMServer legacyWrapper) {
					return PolopolyClient.this.createPolicyCMServer(super.createPolicyCMServer(legacyWrapper), this,
						application, legacyWrapper);
				}
			};
            cmClient.setContentRepositoryClient(contentRepositoryClient);
            cmClient.setUserServiceClient(userServiceClient);

            application.addApplicationComponent(contentRepositoryClient);
            application.addApplicationComponent(userServiceClient);
            return cmClient;
            
        } else {
            EjbCmClient cmClient = new EjbCmClient() {
    				@Override
    				protected PolicyCMServer createPolicyCMServer(CMServer legacyWrapper) {
    					return PolopolyClient.this.createPolicyCMServer(super.createPolicyCMServer(legacyWrapper), this,
    						application, legacyWrapper);
    				}
    			};
            
            return cmClient;
        }

	}

	public static Iterable<ConnectListener> getConnectListeners() {
		Collection<ConnectListener> result = new ArrayList<ConnectListener>();

		try {
			ServiceLoader<ConnectListener> services = ServiceLoader.load(ConnectListener.class);

			Iterator<ConnectListener> serviceIterator = services.iterator();

			while (serviceIterator.hasNext()) {
				result.add(serviceIterator.next());
			}
		} catch (Throwable t) {
			javaUtilLogger.log(Level.WARNING, "While loading connect listeners: " + t.getMessage(), t);
		}

		return result;
	}

	private SolrSearchClient createSolrSearchClient(CmClient cmClient, final StandardApplication app,
		String indexName) throws IllegalApplicationStateException {
		SolrSearchClient result =
			new SolrSearchClient(SolrSearchClient.DEFAULT_MODULE_NAME, "solrClient"
																		+ firstCharacterUppercase(indexName), cmClient);

		result.setIndexName(new SolrIndexName(indexName));

		app.addApplicationComponent(result);

		return result;
	}

	private String firstCharacterUppercase(String indexName) {
		return Character.toUpperCase(indexName.charAt(0)) + indexName.substring(1);
	}

	/**
	 * Intended for overriding.
	 * @param connectionProperties 
	 */
	protected void setUpCmClient(CmClientBase cmClient) {
	}

	/**
	 * Intended for overriding for clients needing to wrap the CM server.
	 */
	protected PolicyCMServer createPolicyCMServer(PolicyCMServer originalServer, CmClient cmClient,
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

	public void addContentFilter(Class<? extends ContentFilter> contentFilterClass) {
		contentFilterClasses.add(contentFilterClass);
	}

	private void loginUserWithoutPassword(PolopolyContext context) throws Exception {
		User user = context.getUserServer().getUserByLoginName(userName);
		UserId userId = user.getUserId();
		context.getPolicyCMServer().setCurrentCaller(new NonLoggedInCaller(userId, null, null, userName));

		logger.info("No password provided. Set caller to user \"" + userName + "\" but did not log in.");
	}

	private void loginUserWithPassword(PolopolyContext context) throws Exception {
		Caller caller =
			context.getUserServer().loginAndMerge(userName, password, context.getPolicyCMServer().getCurrentCaller());

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
			throw new ConnectException("The user " + userName + " to log in could not be found.");
		} catch (AuthenticationFailureException e) {
			throw new ConnectException("The password supplied for user " + userName + " was incorrect.");
		} catch (Exception e) {
			throw new ConnectException("An error occurred while trying to log in user " + userName + ": "
										+ e.getMessage(), e);
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

	public void setAttachHttpFileService(boolean attachHttpFileService) {
	     this.attachHttpFileService = attachHttpFileService;
	}

	public boolean isAttachHttpFileService() {
		return attachHttpFileService;
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

	public void setAttachLRUSynchronizedUpdateCache(boolean attachLRUSynchronizedUpdateCache) {
		this.attachLRUSynchronizedUpdateCache = attachLRUSynchronizedUpdateCache;
	}

	public boolean isAttachLRUSynchronizedUpdateCache() {
		return attachLRUSynchronizedUpdateCache;
	}

	/**
	 * Intended for overriding.
	 */
	protected void setUpLRUSynchronizedUpdateCache(LRUSynchronizedUpdateCache cache) {
	}

	public void setAttachSolrSearchClient(boolean attachSolrSearchClient) {
		this.attachSolrSearchClient = attachSolrSearchClient;
	}

	/**
	 * Add additional index. A common usecase would be a userindex in which case
	 * the parameter would be 'user'.
	 * 
	 * @param indexName
	 *            The name of the index e.g. 'user'.
	 */
	public void addAdditionalIndex(String indexName) {
		additionalIndexes.add(indexName);
	}
}

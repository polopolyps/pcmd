package com.polopoly.util.client;

import static com.polopoly.util.policy.Util.util;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.CreateException;

import com.polopoly.application.Application;
import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.CMServer;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.client.EjbCmClient;
import com.polopoly.cm.client.InputTemplate;
import com.polopoly.cm.client.UserData;
import com.polopoly.cm.client.impl.exceptions.EJBFinderException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.policy.PolicyCMServerAdapter;
import com.polopoly.cm.policy.PolicyCMServerBase;
import com.polopoly.cm.policy.PolicyCMServerWrapper;
import com.polopoly.cm.search.index.RmiSearchClient;
import com.polopoly.poll.client.PollClient;
import com.polopoly.poll.client.PollManager;
import com.polopoly.search.solr.SolrSearchClient;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.User;
import com.polopoly.user.server.UserId;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.CheckedCast;
import com.polopoly.util.CheckedClassCastException;
import com.polopoly.util.Require;
import com.polopoly.util.content.ContentReadUtil;
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.contentid.ContentIdUtil;
import com.polopoly.util.exception.ContentGetException;
import com.polopoly.util.exception.InvalidPolicyClassException;
import com.polopoly.util.exception.NoSuchExternalIdException;
import com.polopoly.util.exception.NoSuchPolicyException;
import com.polopoly.util.exception.NotAcceptedByFilterException;
import com.polopoly.util.exception.PolicyCreateException;
import com.polopoly.util.exception.PolicyGetException;
import com.polopoly.util.exception.PolicyModificationException;
import com.polopoly.util.exception.ServiceUnattachedException;
import com.polopoly.util.exception.UserNotFoundException;
import com.polopoly.util.exception.UserNotLoggedInException;
import com.polopoly.util.policy.PolicyModification;
import com.polopoly.util.policy.PolicyUtil;
import com.polopoly.util.policy.Util;

public class PolopolyContext {

	private static final Logger logger = Logger.getLogger(PolopolyContext.class.getName());

	public static final String INTERNAL_INDEX_NAME = "internal";
	public static final String PUBLIC_INDEX_NAME = "public";

	private PolicyCMServer server;

	private RmiSearchClient searchClient;

	private PollClient pollClient;

	private Map<String, SolrSearchClient> solrSearchClients = new HashMap<String, SolrSearchClient>();

	private CmClient client;

	private Application application;

	private CMServer cmServer;

    private static Map<String, SolrSearchClient> createDefaultIndexMap(SolrSearchClient internalIndexClient, SolrSearchClient publicIndexClient) {
        Map<String, SolrSearchClient> result = new HashMap<String, SolrSearchClient>();
        result.put(PUBLIC_INDEX_NAME, publicIndexClient);
        result.put(INTERNAL_INDEX_NAME, internalIndexClient);
        return Collections.unmodifiableMap(result);
    }
	
	public PolopolyContext(Application application) {
		this((CmClient) application.getApplicationComponent(EjbCmClient.DEFAULT_COMPOUND_NAME),
				(RmiSearchClient) application.getApplicationComponent(RmiSearchClient.DEFAULT_COMPOUND_NAME),
				(PollClient) application.getApplicationComponent(PollClient.DEFAULT_COMPOUND_NAME),
				createDefaultIndexMap(
				(SolrSearchClient) application.getApplicationComponent("search_solrClientInternal"),
				(SolrSearchClient) application.getApplicationComponent(SolrSearchClient.DEFAULT_COMPOUND_NAME)));

		this.application = application;
	}

	public PolopolyContext(CmClient cmClient, RmiSearchClient searchClient, PollClient pollClient,
			Map<String, SolrSearchClient> solrSearchClients) {
		this.client = cmClient;

		if (cmClient != null) {
			this.server = cmClient.getPolicyCMServer();
			this.cmServer = cmClient.getCMServer();
		}

		this.pollClient = pollClient;
		this.searchClient = searchClient;
		this.solrSearchClients = solrSearchClients;
	}

	public PolopolyContext(PolicyCMServer server) {
		this(server, null);
	}
	public PolopolyContext(PolicyCMServer server, Map<String, SolrSearchClient> solrSearchClients) {
		this.server = Require.require(server);
		if(solrSearchClients != null)
			this.solrSearchClients = solrSearchClients;
		
		// get the CM server from the policy CM server.
		PolicyCMServer atServer = server;

		while (cmServer == null && atServer != null) {
			if (atServer instanceof PolicyCMServerWrapper) {
				cmServer = ((PolicyCMServerWrapper) atServer).getCMServer();
			} else if (atServer instanceof PolicyCMServerBase) {
				cmServer = ((PolicyCMServerBase) atServer).getCMServer();
			} else if (atServer instanceof DelegatingPolicyCMServer) {
				atServer = ((DelegatingPolicyCMServer) atServer).getDelegate();
			} else if (atServer instanceof PolicyCMServerAdapter) {
				// we've lost. no way to find it.
				break;
			}
		}
	}

	public PolopolyContext(PolopolyContext context) {
		this.application = context.application;
		this.searchClient = context.searchClient;
		this.client = context.client;
		this.server = context.server;
		this.pollClient = context.pollClient;
		this.solrSearchClients = context.solrSearchClients;
	}

	public CMServer getCMServer() {
		if (cmServer == null) {
			throw new ServiceUnattachedException(
					"CM server not available due to the way the context was constructed.");
		}

		return cmServer;
	}

	public PolicyCMServer getPolicyCMServer() {
		if (server == null) {
			throw new ServiceUnattachedException("CmClient was not attached to the application.");
		}

		return server;
	}

	public Application getApplication() {
		if (application == null) {
			throw new ServiceUnattachedException("No application provided to context constructor.");
		}

		return application;
	}

	public UserServer getUserServer() {
		if (client == null) {
			throw new ServiceUnattachedException("No user server provided to context constructor.");
		}

		return client.getUserServer();
	}

	public PollManager getPollManager() throws ServiceUnattachedException {
		return getPollClient().getPollManager();
	}

	public PollClient getPollClient() throws ServiceUnattachedException {
		if (pollClient == null) {
			throw new ServiceUnattachedException("Poll client");
		}

		return pollClient;
	}

	public SolrSearchClient getSolrSearchClientPublic() throws ServiceUnattachedException {
		
		if (solrSearchClients == null || solrSearchClients.get(PUBLIC_INDEX_NAME) == null) {
			throw new ServiceUnattachedException("SOLR client (public index)");
		}

		return solrSearchClients.get(PUBLIC_INDEX_NAME);
	}

	public SolrSearchClient getSolrSearchClientInternal() throws ServiceUnattachedException {
		if (solrSearchClients == null || solrSearchClients.get(INTERNAL_INDEX_NAME) == null) {
			throw new ServiceUnattachedException("SOLR client (public index)");
		}

		return solrSearchClients.get(INTERNAL_INDEX_NAME);	}

	public CmClient getCmClient() {
		return client;
	}

	public RmiSearchClient getSearchClient() throws ServiceUnattachedException {
		if (searchClient == null) {
			throw new ServiceUnattachedException("Search service");
		}

		return searchClient;
	}

	public Policy createPolicy(int major, String inputTemplate) throws PolicyCreateException {
		return createPolicy(major, inputTemplate, null, Policy.class);
	}

	public <T> T createPolicy(int major, String inputTemplate, Class<T> klass) throws PolicyCreateException {
		return createPolicy(major, inputTemplate, null, klass);
	}

	public Policy createPolicy(int major, ContentId inputTemplate) throws PolicyCreateException {
		return createPolicy(major, inputTemplate, null);
	}

	public <T> T createPolicy(int major, String inputTemplate, ContentId securityParent, Class<T> klass)
			throws PolicyCreateException {
		return createPolicy(major, inputTemplate, securityParent, klass, null);
	}

	public <T> T createPolicy(int major, ContentId inputTemplate, ContentId securityParent, Class<T> klass)
			throws PolicyCreateException {
		return createPolicy(major, inputTemplate, securityParent, klass, null);
	}

	public Policy createPolicy(int major, String inputTemplate, PolicyModification<Policy> modification)
			throws PolicyCreateException {
		return createPolicy(major, inputTemplate, null, Policy.class, modification);
	}

	public Policy createPolicy(int major, ContentId inputTemplate, PolicyModification<Policy> modification)
			throws PolicyCreateException {
		return createPolicy(major, inputTemplate, null, Policy.class, modification);
	}

	public <T> T createPolicy(int major, String inputTemplateName, ContentId securityParent, Class<T> klass,
			PolicyModification<T> modification) throws PolicyCreateException {
		InputTemplate inputTemplate;

		try {
			inputTemplate = getPolicy(inputTemplateName, InputTemplate.class);
		} catch (PolicyGetException e) {
			throw new PolicyCreateException("The input template \"" + inputTemplateName
					+ "\" could not be used: " + e.getMessage(), e);
		}

		return createPolicy(major, inputTemplate.getContentId().getContentId(), securityParent, klass,
				modification);
	}

	public <T> T createPolicy(int major, ContentId inputTemplate, ContentId securityParent, Class<T> klass,
			PolicyModification<T> modification) throws PolicyCreateException {
		T result = null;

		try {
			result = CheckedCast.cast(server.createContent(major, securityParent, inputTemplate), klass);

			if (modification != null) {
				util((Policy) result).modify(modification, klass, false);
			}

			if (logger.isLoggable(Level.INFO)) {
				logger.log(Level.INFO, "Created " + util((Policy) result) + ".");
			}

			return result;
		} catch (PolicyModificationException e) {
			abort((Policy) result, true);

			throw new PolicyCreateException("New object with template " + toString(inputTemplate) + ": "
					+ e.getMessage(), e.getCause());
		} catch (CMException e) {
			abort((Policy) result, true);

			throw new PolicyCreateException("Could not create content with template "
					+ toString(inputTemplate) + ": " + e.getMessage(), e);
		} catch (CheckedClassCastException e) {
			throw new PolicyCreateException("The template " + toString(inputTemplate)
					+ " had an unexpected policy type: " + e.getMessage(), e);
		}
	}

	private void abort(Policy policy, boolean removeContentVersion) {
		try {
			server.abortContent(policy, true);
		} catch (CMException e1) {
		}
	}

	public Policy getPolicy(String externalId) throws PolicyGetException {
		return getPolicy(externalId, Policy.class);
	}

	public PolicyUtil getPolicyUtil(String externalId) throws PolicyGetException {
		return util(getPolicy(externalId, Policy.class));
	}

	public <T> T getPolicy(String externalId, Class<T> klass) throws PolicyGetException {
		return getPolicy(new ExternalContentId(externalId), klass);
	}

	public PolicyUtil getPolicyUtil(ContentId contentId) throws PolicyGetException {
		return util(getPolicy(contentId, Policy.class));
	}

	public Policy getPolicy(ContentId contentId) throws PolicyGetException {
		return getPolicy(contentId, Policy.class);
	}

	public <T> T getPolicy(ContentId contentId, Class<T> klass) throws PolicyGetException {
		return getPolicy(getPolicyCMServer(), contentId, klass);
	}

	private static String toString(ContentId contentId) {
		if (contentId instanceof ExternalContentId) {
			return ((ExternalContentId) contentId).getExternalId();
		} else {
			return contentId.getContentIdString();
		}
	}

	public static <T> T getPolicy(PolicyCMServer server, ContentId contentId, Class<T> klass)
			throws PolicyGetException {
		if (contentId == null) {
			throw new PolicyGetException("No content ID supplied.");
		}

		try {
			return CheckedCast.cast(server.getPolicy(contentId), klass);
		} catch (EJBFinderException e) {
			if (e.getMessage().contains("not accepted by filter")) {
				throw new NotAcceptedByFilterException("The policy " + toString(contentId)
						+ " cannot be accessed due to the current content filters: " + e.getMessage(), e);
			}

			throw new NoSuchPolicyException("The policy " + toString(contentId) + " could not be found.", e);
		} catch (CMException e) {
			throw new PolicyGetException("While fetching policy " + toString(contentId) + ": "
					+ e.getMessage(), e);
		} catch (CMRuntimeException e) {
			throw new InvalidPolicyClassException("While fetching policy " + toString(contentId) + ": "
					+ e.getMessage(), e);
		} catch (CheckedClassCastException e) {
			throw new InvalidPolicyClassException("While fetching policy " + toString(contentId) + ": "
					+ e.getMessage(), e);
		}
	}

	public ContentUtil getContent(ContentId contentId) throws ContentGetException {
		try {
			return util(getPolicyCMServer().getContent(contentId), this);
		} catch (CMException e) {
			throw new ContentGetException("While fetching content " + toString(contentId) + ": "
					+ e.getMessage(), e);
		}
	}

	public ContentReadUtil getContent(String externalId) throws ContentGetException {
		return getContent(new ExternalContentId(externalId));
	}

	public User getCurrentUserServerUser() throws UserNotLoggedInException {
		Caller caller = getPolicyCMServer().getCurrentCaller();

		if (caller == null) {
			throw new UserNotLoggedInException();
		}

		UserId userId = caller.getUserId();

		if (userId == null) {
			throw new UserNotLoggedInException();
		}

		try {
			return getUserServer().getUserByUserId(userId);
		} catch (RemoteException e) {
			logger.log(Level.WARNING, e.getMessage(), e);

			throw new UserNotLoggedInException(e);
		} catch (CreateException e) {
			logger.log(Level.WARNING, e.getMessage(), e);

			throw new UserNotLoggedInException(e);
		}
	}

	public UserData getCurrentUser() throws UserNotLoggedInException {
		return getCurrentUser(UserData.class);
	}

	public <T> T getCurrentUser(Class<T> userClass) throws UserNotLoggedInException {
		Caller caller = getPolicyCMServer().getCurrentCaller();

		if (caller == null) {
			throw new UserNotLoggedInException();
		}

		UserId userId = caller.getUserId();

		if (userId == null) {
			throw new UserNotLoggedInException();
		}

		try {
			return getUser(userId, userClass);
		} catch (UserNotFoundException e) {
			logger.log(Level.WARNING, e.getMessage(), e);

			throw new UserNotLoggedInException(e);
		}

	}

	/**
	 * Returns the policy of the specified user.
	 */
	public <T> T getUser(UserId userId, Class<T> userClass) throws UserNotFoundException {
		try {
			return getPolicy(userId.getPrincipalIdString(), userClass);
		} catch (PolicyGetException e) {
			String message = "Fetching current user with principal ID " + userId.getPrincipalIdString()
					+ ": " + e.getMessage();

			logger.log(Level.WARNING, message, e);

			throw new UserNotFoundException(message, e);
		}
	}

	public ContentIdUtil resolveExternalId(String externalId) throws NoSuchExternalIdException {
		if (externalId == null) {
			throw new NoSuchExternalIdException(externalId);
		}

		VersionedContentId contentId;

		try {
			contentId = getPolicyCMServer().findContentIdByExternalId(new ExternalContentId(externalId));
		} catch (CMException e) {
			logger.log(Level.WARNING,
					"While resolving external ID \"" + externalId + "\": " + e.getMessage(), e);

			throw new NoSuchExternalIdException(externalId, e);
		}

		if (contentId == null) {
			throw new NoSuchExternalIdException(externalId);
		}

		return Util.util(contentId, this);
	}

	/**
	 * Retrieves the solrSearch client matching the indexName set in the context or in the app.
	 * 
	 * @param indexName
	 *            name of the index for the solrSearchClient.
	 * @return
	 */
	public SolrSearchClient getSolrSearchClient(String indexName) {
		SolrSearchClient client = solrSearchClients.get(indexName);
		if(client == null && application != null) {
			String compName = "search_solrClient" + Character.toUpperCase(indexName.charAt(0)) + indexName.substring(1);
			client = (SolrSearchClient) application.getApplicationComponent(compName);
		}
		return client;
	}

}

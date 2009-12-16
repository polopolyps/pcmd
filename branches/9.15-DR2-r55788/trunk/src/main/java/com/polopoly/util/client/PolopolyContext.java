package com.polopoly.util.client;

import static com.polopoly.util.policy.Util.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.application.Application;
import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.client.EjbCmClient;
import com.polopoly.cm.client.InputTemplate;
import com.polopoly.cm.client.UserData;
import com.polopoly.cm.client.impl.exceptions.EJBFinderException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.search.index.RmiSearchClient;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.UserId;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.CheckedCast;
import com.polopoly.util.CheckedClassCastException;
import com.polopoly.util.content.ContentReadUtil;
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.contentid.ContentIdUtil;
import com.polopoly.util.exception.ContentGetException;
import com.polopoly.util.exception.InvalidPolicyClassException;
import com.polopoly.util.exception.NoSuchExternalIdException;
import com.polopoly.util.exception.NoSuchPolicyException;
import com.polopoly.util.exception.PolicyCreateException;
import com.polopoly.util.exception.PolicyGetException;
import com.polopoly.util.exception.PolicyModificationException;
import com.polopoly.util.exception.UserNotLoggedInException;
import com.polopoly.util.policy.PolicyModification;
import com.polopoly.util.policy.PolicyUtil;
import com.polopoly.util.policy.Util;

public class PolopolyContext {
    private static final Logger logger = Logger.getLogger(PolopolyContext.class
            .getName());

    private PolicyCMServer server;

    private RmiSearchClient searchClient;

    private CmClient client;

    private Application application;

    public PolopolyContext(Application application) {
        this(
                (CmClient) application
                        .getApplicationComponent(EjbCmClient.DEFAULT_COMPOUND_NAME),
                (RmiSearchClient) application
                        .getApplicationComponent(RmiSearchClient.DEFAULT_COMPOUND_NAME));

        this.application = application;
    }

    public PolopolyContext(CmClient cmClient, RmiSearchClient searchClient) {
        this.client = cmClient;
        if (cmClient != null) {
            this.server = cmClient.getPolicyCMServer();
        }
        this.searchClient = searchClient;
    }

    public PolopolyContext(PolicyCMServer server) {
        this.server = server;
    }

    public PolicyCMServer getPolicyCMServer() {
        return server;
    }

    public Application getApplication() {
        if (application == null) {
            throw new CMRuntimeException(
                    "No application provided to context constructor.");
        }

        return application;
    }

    public UserServer getUserServer() {
        if (client == null) {
            throw new CMRuntimeException(
                    "No user server provided to context constructor.");
        }

        return client.getUserServer();
    }

    public CmClient getCmClient() {
        return client;
    }

    public RmiSearchClient getSearchClient() {
        if (searchClient == null) {
            throw new CMRuntimeException("Search service is not attached.");
        }

        return searchClient;
    }

    public Policy createPolicy(int major, String inputTemplate)
            throws PolicyCreateException {
        return createPolicy(major, inputTemplate, null, Policy.class);
    }

    public <T> T createPolicy(int major, String inputTemplate, Class<T> klass)
            throws PolicyCreateException {
        return createPolicy(major, inputTemplate, null, klass);
    }

    public Policy createPolicy(int major, ContentId inputTemplate)
            throws PolicyCreateException {
        return createPolicy(major, inputTemplate, null);
    }

    public <T> T createPolicy(int major, String inputTemplate,
            ContentId securityParent, Class<T> klass)
            throws PolicyCreateException {
        return createPolicy(major, inputTemplate, securityParent, klass, null);
    }

    public <T> T createPolicy(int major, ContentId inputTemplate,
            ContentId securityParent, Class<T> klass)
            throws PolicyCreateException {
        return createPolicy(major, inputTemplate, securityParent, klass, null);
    }

    public Policy createPolicy(int major, String inputTemplate,
            PolicyModification<Policy> modification)
            throws PolicyCreateException {
        return createPolicy(major, inputTemplate, null, Policy.class,
                modification);
    }

    public Policy createPolicy(int major, ContentId inputTemplate,
            PolicyModification<Policy> modification)
            throws PolicyCreateException {
        return createPolicy(major, inputTemplate, null, Policy.class,
                modification);
    }

    public <T> T createPolicy(int major, String inputTemplateName,
            ContentId securityParent, Class<T> klass,
            PolicyModification<T> modification) throws PolicyCreateException {
        InputTemplate inputTemplate;

        try {
            inputTemplate = getPolicy(inputTemplateName, InputTemplate.class);
        } catch (PolicyGetException e) {
            throw new PolicyCreateException("The input template \""
                    + inputTemplateName + "\" could not be used: "
                    + e.getMessage(), e);
        }

        return createPolicy(major, inputTemplate.getContentId().getContentId(),
                securityParent, klass, modification);
    }

    public <T> T createPolicy(int major, ContentId inputTemplate,
            ContentId securityParent, Class<T> klass,
            PolicyModification<T> modification) throws PolicyCreateException {
        try {
            T result = CheckedCast.cast(server.createContent(major,
                    securityParent, inputTemplate), klass);

            if (modification != null) {
                util((Policy) result).modify(modification, klass, false);
            }

            if (logger.isLoggable(Level.INFO)) {
                logger
                        .log(Level.INFO, "Created " + util((Policy) result)
                                + ".");
            }

            return result;
        } catch (PolicyModificationException e) {
            throw new PolicyCreateException("New object with template "
                    + toString(inputTemplate) + ": " + e.getMessage(), e
                    .getCause());
        } catch (CMException e) {
            throw new PolicyCreateException(
                    "Could not create content with template "
                            + toString(inputTemplate) + ": " + e.getMessage(),
                    e);
        } catch (CheckedClassCastException e) {
            throw new PolicyCreateException("The template "
                    + toString(inputTemplate)
                    + " had an unexpected policy type: " + e.getMessage(), e);
        }
    }

    public Policy getPolicy(String externalId) throws PolicyGetException {
        return getPolicy(externalId, Policy.class);
    }

    public PolicyUtil getPolicyUtil(String externalId)
            throws PolicyGetException {
        return util(getPolicy(externalId, Policy.class));
    }

    public <T> T getPolicy(String externalId, Class<T> klass)
            throws PolicyGetException {
        return getPolicy(new ExternalContentId(externalId), klass);
    }

    public PolicyUtil getPolicyUtil(ContentId contentId)
            throws PolicyGetException {
        return util(getPolicy(contentId, Policy.class));
    }

    public Policy getPolicy(ContentId contentId) throws PolicyGetException {
        return getPolicy(contentId, Policy.class);
    }

    public <T> T getPolicy(ContentId contentId, Class<T> klass)
            throws PolicyGetException {
        return getPolicy(getPolicyCMServer(), contentId, klass);
    }

    private static String toString(ContentId contentId) {
        if (contentId instanceof ExternalContentId) {
            return ((ExternalContentId) contentId).getExternalId();
        } else {
            return contentId.getContentIdString();
        }
    }

    public static <T> T getPolicy(PolicyCMServer server, ContentId contentId,
            Class<T> klass) throws PolicyGetException {
        if (contentId == null) {
            throw new PolicyGetException("No content ID supplied.");
        }

        try {
            return CheckedCast.cast(server.getPolicy(contentId), klass);
        } catch (EJBFinderException e) {
            throw new NoSuchPolicyException("The policy " + toString(contentId)
                    + " could not be found.", e);
        } catch (CMException e) {
            throw new PolicyGetException("While fetching policy "
                    + toString(contentId) + ": " + e.getMessage(), e);
        } catch (CMRuntimeException e) {
            throw new InvalidPolicyClassException("While fetching policy "
                    + toString(contentId) + ": " + e.getMessage(), e);
        } catch (CheckedClassCastException e) {
            throw new InvalidPolicyClassException("While fetching policy "
                    + toString(contentId) + ": " + e.getMessage(), e);
        }
    }

    public ContentUtil getContent(ContentId contentId)
            throws ContentGetException {
        try {
            return util(getPolicyCMServer().getContent(contentId), this);
        } catch (CMException e) {
            throw new ContentGetException("While fetching content "
                    + toString(contentId) + ": " + e.getMessage(), e);
        }
    }

    public ContentReadUtil getContent(String externalId)
            throws ContentGetException {
        return getContent(new ExternalContentId(externalId));
    }

    public UserData getCurrentUser() throws UserNotLoggedInException {
        return getCurrentUser(UserData.class);
    }

    public <T> T getCurrentUser(Class<T> userClass)
            throws UserNotLoggedInException {
        Caller caller = getPolicyCMServer().getCurrentCaller();

        if (caller == null) {
            throw new UserNotLoggedInException();
        }

        UserId userId = caller.getUserId();

        if (userId == null) {
            throw new UserNotLoggedInException();
        }

        return getUser(userId, userClass);
    }

    /**
     * Returns the policy of the specified user.
     */
    public <T> T getUser(UserId userId, Class<T> userClass)
            throws UserNotLoggedInException {
        try {
            return getPolicy(userId.getPrincipalIdString(), userClass);
        } catch (PolicyGetException e) {
            logger.log(Level.WARNING,
                    "Fetching current user with principal ID "
                            + userId.getPrincipalIdString() + ": "
                            + e.getMessage(), e);

            throw new UserNotLoggedInException();
        }
    }

    public ContentIdUtil resolveExternalId(String externalId)
            throws NoSuchExternalIdException {
        if (externalId == null) {
            throw new NoSuchExternalIdException(externalId);
        }

        VersionedContentId contentId;

        try {
            contentId = getPolicyCMServer().findContentIdByExternalId(
                    new ExternalContentId(externalId));
        } catch (CMException e) {
            logger.log(Level.WARNING, "While resolving external ID \""
                    + externalId + "\": " + e.getMessage(), e);

            throw new NoSuchExternalIdException(externalId, e);
        }

        if (contentId == null) {
            throw new NoSuchExternalIdException(externalId);
        }

        return Util.util(contentId, this);
    }

}

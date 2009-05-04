package com.polopoly.util.client;

import static com.polopoly.util.policy.Util.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.application.Application;
import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.client.EjbCmClient;
import com.polopoly.cm.client.InputTemplate;
import com.polopoly.cm.client.UserData;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.search.index.RmiSearchClient;
import com.polopoly.user.server.Caller;
import com.polopoly.user.server.UserId;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.CheckedCast;
import com.polopoly.util.CheckedClassCastException;
import com.polopoly.util.exception.ContentGetException;
import com.polopoly.util.exception.PolicyCreateException;
import com.polopoly.util.exception.PolicyGetException;
import com.polopoly.util.exception.PolicyModificationException;
import com.polopoly.util.exception.UserNotLoggedInException;
import com.polopoly.util.policy.PolicyModification;

public class PolopolyContext {
    private static final Logger logger =
        Logger.getLogger(PolopolyContext.class.getName());

    private PolicyCMServer server;
    private RmiSearchClient searchClient;
    private CmClient client;

    public PolopolyContext(Application application) {
        this((CmClient) application.getApplicationComponent(EjbCmClient.DEFAULT_COMPOUND_NAME),
             (RmiSearchClient) application.getApplicationComponent(RmiSearchClient.DEFAULT_COMPOUND_NAME));
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

    public UserServer getUserServer() {
        if (client == null) {
            throw new CMRuntimeException("No user server provided to context constructor.");
        }

        return client.getUserServer();
    }

    public RmiSearchClient getSearchClient() {
        if (searchClient == null) {
            throw new CMRuntimeException("Search service is not attached.");
        }

        return searchClient;
    }

    public Policy createPolicy(int major, String inputTemplate) throws PolicyCreateException {
        return createPolicy(major, inputTemplate, null);
    }

    public Policy createPolicy(int major, ContentId inputTemplate) throws PolicyCreateException {
        return createPolicy(major, inputTemplate, null);
    }

    public <T> T createPolicy(int major, String inputTemplate, ContentId securityParent, Class<T> klass) throws PolicyCreateException {
        return createPolicy(major, inputTemplate, securityParent, klass, null);
    }

    public <T> T createPolicy(int major, ContentId inputTemplate, ContentId securityParent, Class<T> klass) throws PolicyCreateException {
        return createPolicy(major, inputTemplate, securityParent, klass, null);
    }

    public Policy createPolicy(int major, String inputTemplate, PolicyModification<Policy> modification) throws PolicyCreateException {
        return createPolicy(major, inputTemplate, null, Policy.class);
    }

    public Policy createPolicy(int major, ContentId inputTemplate, PolicyModification<Policy> modification) throws PolicyCreateException {
        return createPolicy(major, inputTemplate, null, Policy.class);
    }

    public <T> T createPolicy(int major, String inputTemplate, ContentId securityParent, Class<T> klass, PolicyModification<T> modification) throws PolicyCreateException {
        try {
            return createPolicy(major, getPolicy(inputTemplate, InputTemplate.class).getContentId().getContentId(), securityParent, klass);
        } catch (PolicyGetException e) {
            throw new PolicyCreateException("The input template \"" + inputTemplate + "\" could not be used: " + e.getMessage(), e);
        }
    }

    public <T> T createPolicy(int major, ContentId inputTemplate, ContentId securityParent, Class<T> klass, PolicyModification<T> modification) throws PolicyCreateException {
        try {
            T result = CheckedCast.cast(server.createContent(major, inputTemplate, securityParent), klass);

            if (modification != null) {
                util((Policy) result).modify(modification, klass, false);
            }

            return result;
        } catch (PolicyModificationException e) {
            throw new PolicyCreateException("New object with template " + inputTemplate.getContentIdString() + ": " + e.getMessage());
        } catch (CMException e) {
            throw new PolicyCreateException("Could not create content with template " + inputTemplate.getContentIdString() + ": " + e.getMessage(), e);
        } catch (CheckedClassCastException e) {
            throw new PolicyCreateException("The template " + inputTemplate.getContentIdString() + " had an unexpected policy type: " + e.getMessage(), e);
        }
    }

    public Policy getPolicy(String externalId) throws PolicyGetException {
        return getPolicy(externalId, Policy.class);
    }

    public <T> T getPolicy(String externalId, Class<T> klass) throws PolicyGetException {
        return getPolicy(new ExternalContentId(externalId), klass);
    }

    public Policy getPolicy(ContentId contentId) throws PolicyGetException {
        return getPolicy(contentId, Policy.class);
    }

    public <T> T getPolicy(ContentId contentId, Class<T> klass) throws PolicyGetException {
        try {
            return getPolicy(getPolicyCMServer(), contentId, klass);
        } catch (CMException e) {
            throw new PolicyGetException("While fetching policy " + contentId.getContentIdString() + ": " + e.getMessage(), e);
        }
    }

    public static <T> T getPolicy(PolicyCMServer server, ContentId contentId, Class<T> klass) throws PolicyGetException {
        if (contentId == null) {
            throw new PolicyGetException("No content ID supplied.");
        }

        try {
            return CheckedCast.cast(server.getPolicy(contentId), klass);
        } catch (CMException e) {
            throw new PolicyGetException("While fetching policy " + contentId.getContentIdString() + ": " + e.getMessage(), e);
        } catch (CheckedClassCastException e) {
            throw new PolicyGetException("While fetching policy " + contentId.getContentIdString() + ": " + e.getMessage(), e);
        }
    }

    public ContentRead getContent(ContentId contentId) throws ContentGetException {
        try {
            return getPolicyCMServer().getContent(contentId);
        } catch (CMException e) {
            throw new ContentGetException("While fetching content " + contentId.getContentIdString() + ": " + e.getMessage(), e);
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
            logger.log(Level.WARNING, "Fetching current user with principal ID " +
                    userId.getPrincipalIdString() + ": " + e.getMessage(), e);

            throw new UserNotLoggedInException();
        }
    }
}

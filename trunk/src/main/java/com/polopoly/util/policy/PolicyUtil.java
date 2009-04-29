package com.polopoly.util.policy;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.LockInfo;
import com.polopoly.cm.app.policy.SingleReference;
import com.polopoly.cm.app.policy.SingleValued;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.ContentListAware;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.util.CheckedCast;
import com.polopoly.util.CheckedClassCastException;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.FetchingIterator;
import com.polopoly.util.exception.PolicyGetException;
import com.polopoly.util.exception.PolicyModificationException;

public class PolicyUtil implements Iterable<Policy> {
    private Policy policy;

    private static final Logger logger =
        Logger.getLogger(PolicyUtil.class.getName());

    /**
     * Use {@link Util#util(Policy)} to get an instance.
     */
    PolicyUtil(Policy policy) {
        this.policy = policy;
    }

    public void setSingleValued(String field, String value) {
        try {
            getChildPolicy(field, SingleValued.class).setValue(value);
        } catch (CMException e) {
            throw new CMRuntimeException(
                    "Could not set field " + field + " in " + this + ": " + e.getMessage());
        }
    }

    public String getSingleValued(String field, String defaultValue) {
        String result = null;

        try {
            result = getSingleValued(field);
        } catch (CMRuntimeException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }

        if (result == null) {
            return defaultValue;
        }

        return result;
    }

    public String getSingleValued(String field) {
        try {
            return getChildPolicy(field, SingleValued.class).getValue();
        } catch (CMException e) {
            throw new CMRuntimeException(
                    "Could not get field " + field + " in " + this + ": " + e.getMessage(), e);
        }
    }

    public <T> T getSingleReference(String field, Class<T> policyClass) throws PolicyGetException {
        try {
            return PolopolyContext.getPolicy(getCMServer(), getSingleReference(field), policyClass);
        } catch (PolicyGetException e) {
            throw new PolicyGetException("While getting field " + field +
                    " in " + this + ": " + e.getMessage(), e.getCause());
        }
    }

    public ContentId getSingleReference(String field) throws PolicyGetException {
        try {
            return getChildPolicy(field, SingleReference.class).getReference();
        } catch (CMException e) {
            throw new PolicyGetException(
                "Could not get field " + field + " in " + this + ": " + e.getMessage(), e);
        }
    }

    public ContentListUtil getContentListAware(String field) {
        try {
            return new ContentListUtil(getChildPolicy(field, ContentListAware.class).getContentList(), this, getCMServer());
        } catch (CMException e) {
            throw new CMRuntimeException("While getting content list of field " + field + " in " + this + ": " + e.getMessage(), e);
        }
    }

    public ContentUtil getContent() {
        return new ContentUtil(policy.getContent(), getCMServer());
    }

    public <T> T getChildPolicy(String field, Class<T> klass) {
        try {
            return CheckedCast.cast(policy.getChildPolicy(field), klass);
        } catch (Exception e) {
            String inputTemplate;
            try {
                inputTemplate = policy.getInputTemplate().getExternalId().getExternalId();
            } catch (CMException e2) {
                inputTemplate = e2.toString();
            }

            throw new CMRuntimeException("Could not get field " + field + " in " + this + " (a " + inputTemplate + ") : " + e.getMessage());
        }
    }

    public void log(Level level, String message) {
        getLogger().log(level, message);
    }

    private Logger getLogger() {
        return Logger.getLogger(policy.getClass().getName());
    }

    public <T> void modify(PolicyModification<T> policyModification, Class<T> klass) throws PolicyModificationException {
        modify(policyModification, klass);
    }

    public <T> void modify(PolicyModification<T> policyModification, Class<T> klass, boolean createNewVersion) throws PolicyModificationException {
        LockInfo lockInfo = policy.getContent().getLockInfo();
        PolicyUtil util = new PolicyUtil(policy);
        PolicyCMServer server = util.getCMServer();

        if (createNewVersion) {
            if (lockInfo != null) {
                throw new PolicyModificationException(
                        "Content " + policy.getContentId().getContentId().getContentIdString() + " was locked.");
            }

            try {
                policy = server.createContentVersion(policy.getContentId());
            } catch (CMException e) {
                throw new PolicyModificationException(
                        "While creating new version of " + policy.getContentId().getContentId().getContentIdString() +
                        ": " + e.getMessage(), e);
            }
        }
        else {
            try {
                if (lockInfo == null || !lockInfo.isLockedBy(server.getCurrentCaller())) {
                    throw new PolicyModificationException(
                            "Content " + policy.getContentId().getContentId().getContentIdString() + " was not locked by current caller.");
                }
            } catch (CMException e) {
                throw new PolicyModificationException(
                        "While determining if " + util.toString() +
                        " was locked: " + e.getMessage(), e);
            }
        }

        try {
            policyModification.modify(CheckedCast.cast(policy, klass));
        } catch (CMException e) {
            abort(server, createNewVersion);

            throw new PolicyModificationException("While modifying " +
                    policy.getContentId().getContentId().getContentIdString() + ": " + e.getMessage(), e);
        } catch (RuntimeException e) {
            abort(server, createNewVersion);

            throw e;
        } catch (CheckedClassCastException e) {
            abort(server, createNewVersion);

            throw new PolicyModificationException("New version of " +
                    policy.getContentId().getContentId().getContentIdString() + ": " + e.getMessage(), e);
        }

        try {
            policy.getContent().commit();
        } catch (CMException e) {
            abort(server, createNewVersion);

            throw new PolicyModificationException("While modifying " +
                    policy.getContentId().getContentId().getContentIdString() + ": " + e.getMessage(), e);
        }
    }

    private void abort(PolicyCMServer server, boolean createNewVersion) {
        try {
            server.abortContent(policy, createNewVersion);
        } catch (CMException e1) {
        }
    }

    private PolicyCMServer getCMServer() {
        try {
            return policy.getCMServer();
        } catch (CMException e) {
            throw new CMRuntimeException("While getting CM server of " + this + ": " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public Iterator<Policy> iterator() {
        try {
            return new FetchingIterator<Policy>() {
                Iterator<String> names = policy.getChildPolicyNames().iterator();

                @Override
                public void remove() {
                }

                @Override
                protected Policy fetch() {
                    if (names.hasNext()) {
                        String childPolicyName = names.next();

                        try {
                            return policy.getChildPolicy(childPolicyName);
                        } catch (CMException e) {
                            logger.log(Level.WARNING, "While getting child policy " + childPolicyName + " of " + this + ": " + e.getMessage(), e);

                            return fetch();
                        }
                    }

                    return null;
                }};
        } catch (CMException e) {
            logger.log(Level.WARNING, "While getting child policy names of " + this + ": " + e.getMessage(), e);

            List<Policy> emptyList = Collections.emptyList();
            return emptyList.iterator();
        }
    }

    @Override
    public String toString() {
        String name;

        try {
            name = policy.getPolicyName();
        } catch (CMException e1) {
            name = null;
        }

        if ("".equals(name) || name == null) {
            try {
                name = policy.getContent().getName();
            } catch (CMException e) {
                name = e.toString();
            }

            return name +
                " (" + policy.getContentId().getContentId().getContentIdString() + ")";
        }
        else {
            return name + " in " + policy.getContentId().getContentId().getContentIdString();
        }
    }
}

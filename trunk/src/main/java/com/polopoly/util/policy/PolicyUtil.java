package com.polopoly.util.policy;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.LockInfo;
import com.polopoly.cm.app.policy.SingleValued;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.pcmd.util.CheckedCast;
import com.polopoly.pcmd.util.CheckedClassCastException;
import com.polopoly.util.collection.FetchingIterator;

public class PolicyUtil implements Iterable<Policy> {
    private Policy policy;

    private static final Logger logger =
        Logger.getLogger(PolicyUtil.class.getName());

    public PolicyUtil(Policy policy) {
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

    public String getSingleValued(String field) {
        try {
            return getChildPolicy(field, SingleValued.class).getValue();
        } catch (CMException e) {
            throw new CMRuntimeException(
                    "Could not get field " + field + " in " + this + ": " + e.getMessage(), e);
        }
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

    public <T> T getPolicy(ContentId contentId, Class<T> klass) throws PolicyGetException {
        try {
            return getPolicy(policy.getCMServer(), contentId, klass);
        } catch (CMException e) {
            throw new PolicyGetException("While fetching policy " + contentId.getContentIdString() + ": " + e.getMessage(), e);
        }
    }

    public static <T> T getPolicy(PolicyCMServer server, ContentId contentId, Class<T> klass) throws PolicyGetException {
        try {
            return CheckedCast.cast(server.getPolicy(contentId), klass);
        } catch (CMException e) {
            throw new PolicyGetException("While fetching policy " + contentId.getContentIdString() + ": " + e.getMessage(), e);
        } catch (CheckedClassCastException e) {
            throw new PolicyGetException("While fetching policy " + contentId.getContentIdString() + ": " + e.getMessage(), e);
        }
    }

    public <T> void modify(PolicyModification<T> policyModification, Class<T> klass) throws PolicyModificationException {
        modify(policy, policyModification, klass);
    }

    public <T> void modify(PolicyModification<T> policyModification, Class<T> klass, boolean createNewVersion) throws PolicyModificationException {
        modify(policy, policyModification, klass, createNewVersion);
    }

    public static <T> void modify(Policy policy, PolicyModification<T> policyModification, Class<T> klass) throws PolicyModificationException {
        modify(policy, policyModification, klass, false);
    }

    public static <T> void modify(Policy policy, PolicyModification<T> policyModification, Class<T> klass, boolean createNewVersion) throws PolicyModificationException {
        LockInfo lockInfo = policy.getContent().getLockInfo();

        if (createNewVersion) {
            if (lockInfo != null) {
                throw new PolicyModificationException(
                        "Content " + policy.getContentId().getContentId().getContentIdString() + " was locked.");
            }

            try {
                policy = policy.getCMServer().createContentVersion(policy.getContentId());
            } catch (CMException e) {
                throw new PolicyModificationException(
                        "While creating new version of " + policy.getContentId().getContentId().getContentIdString() +
                        ": " + e.getMessage(), e);
            }
        }
        else {
            try {
                if (lockInfo == null || !lockInfo.isLockedBy(policy.getCMServer().getCurrentCaller())) {
                    throw new PolicyModificationException(
                            "Content " + policy.getContentId().getContentId().getContentIdString() + " was not locked by current caller.");
                }
            } catch (CMException e) {
                throw new PolicyModificationException(
                        "While determining if " + policy.getContentId().getContentId().getContentIdString() +
                        " was locked: " + e.getMessage(), e);
            }
        }

        try {
            policyModification.modify(CheckedCast.cast(policy, klass));
        } catch (CMException e) {
            try {
                policy.getCMServer().abortContent(policy);
            } catch (CMException e1) {
            }

            throw new PolicyModificationException("While modifying " +
                    policy.getContentId().getContentId().getContentIdString() + ": " + e.getMessage(), e);
        } catch (RuntimeException e) {
            try {
                policy.getCMServer().abortContent(policy);
            } catch (CMException e1) {
            }

            throw e;
        } catch (CheckedClassCastException e) {
            throw new PolicyModificationException("New version of " +
                    policy.getContentId().getContentId().getContentIdString() + ": " + e.getMessage(), e);
        }

        try {
            policy.getContent().commit();
        } catch (CMException e) {
            try {
                policy.getCMServer().abortContent(policy);
            } catch (CMException e1) {
            }

            throw new PolicyModificationException("While modifying " +
                    policy.getContentId().getContentId().getContentIdString() + ": " + e.getMessage(), e);
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

package com.polopoly.util.policy;

import static com.polopoly.util.client.PolopolyContext.getPolicy;
import static com.polopoly.util.policy.Util.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.LockInfo;
import com.polopoly.cm.app.policy.CheckboxPolicy;
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
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.contentid.ContentIdUtil;
import com.polopoly.util.contentlist.ContentListUtil;
import com.polopoly.util.contentlist.ContentListUtilImpl;
import com.polopoly.util.exception.EmptyListException;
import com.polopoly.util.exception.InvalidPolicyClassException;
import com.polopoly.util.exception.InvalidTopPolicyClassException;
import com.polopoly.util.exception.NoSuchChildPolicyException;
import com.polopoly.util.exception.PolicyDeleteException;
import com.polopoly.util.exception.PolicyGetException;
import com.polopoly.util.exception.PolicyModificationException;
import com.polopoly.util.exception.ReferenceNotSetException;

public class PolicyUtilImpl extends RuntimeExceptionPolicyWrapper implements
        PolicyUtil {
    private Policy policy;

    private PolopolyContext context;

    private ContentUtil contentCache;

    private static final Logger logger = Logger.getLogger(PolicyUtilImpl.class
            .getName());

    /**
     * Use {@link Util#util(Policy)} to get an instance.
     */
    public PolicyUtilImpl(Policy policy) {
        super(policy);

        this.policy = policy;
    }

    public PolicyUtilImpl(Policy policy, PolopolyContext context) {
        this(policy);

        this.context = context;
    }

    public PolopolyContext getContext() {
        if (context == null) {
            context = util(getCMServer());
        }

        return context;
    }

    public String getName() {
        return getContent().getName();
    }

    public void setSingleValued(String field, String value)
            throws NoSuchChildPolicyException {
        try {
            getChildPolicy(field, SingleValued.class).setValue(value);
        } catch (CMException e) {
            throw new CMRuntimeException("Could not set field " + field
                    + " in " + this + ": " + e.getMessage());
        }
    }

    public String getSingleValued(String field, String defaultValue)
            throws NoSuchChildPolicyException {
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

    public String getSingleValued(String field)
            throws NoSuchChildPolicyException {
        try {
            return getChildPolicy(field, SingleValued.class).getValue();
        } catch (CMException e) {
            throw new CMRuntimeException("Could not get field " + field
                    + " in " + this + ": " + e.getMessage(), e);
        }
    }

    public boolean getChecked(String field) throws NoSuchChildPolicyException {
        try {
            return getChildPolicy(field, CheckboxPolicy.class).getChecked();
        } catch (CMException e) {
            throw new CMRuntimeException("Could not get field " + field
                    + " in " + this + ": " + e.getMessage(), e);
        }
    }

    public void setChecked(String field, boolean checked)
            throws NoSuchChildPolicyException {
        try {
            getChildPolicy(field, CheckboxPolicy.class).setChecked(checked);
        } catch (CMException e) {
            throw new CMRuntimeException("While settign field " + field
                    + " in " + this + ": " + e.getMessage(), e);
        }
    }

    public void setSingleReference(String field, Policy policy) {
        try {
            getChildPolicy(field, SingleReference.class).setReference(
                    policy.getContentId().getContentId());
        } catch (CMException e) {
            throw new CMRuntimeException("While setting field " + field
                    + " in " + this + " to " + util(policy) + ": "
                    + e.getMessage(), e);
        }
    }

    public void setSingleReference(Policy reference) {
        try {
            ContentId contentId;

            if (reference == null) {
                contentId = null;
            } else {
                contentId = reference.getContentId().getContentId();
            }

            CheckedCast.cast(this.policy, SingleReference.class).setReference(
                    contentId);
        } catch (CMException e) {
            throw new CMRuntimeException("While setting reference in " + this
                    + " to " + util(reference) + ": " + e.getMessage(), e);
        } catch (CheckedClassCastException e) {
            throw new CMRuntimeException(this + " was of unexpected type: "
                    + e.getMessage());
        }
    }

    public <T> T getSingleReference(String field, Class<T> policyClass)
            throws PolicyGetException, ReferenceNotSetException,
            NoSuchChildPolicyException {
        ContentIdUtil reference = null;

        try {
            reference = getSingleReference(field);

            if (reference == null) {
                throw new ReferenceNotSetException("Field " + field
                        + " was not set in " + this + ".");
            }

            try {
                return getPolicy(getCMServer(), reference, policyClass);
            } catch (PolicyGetException defaultVersionException) {
                try {
                    // retry with latest version.
                    return reference.getLatestVersion().asPolicy(policyClass);
                } catch (PolicyGetException latestException) {
                    throw new PolicyGetException("While getting field " + field
                            + " in " + this + ": "
                            + defaultVersionException.getMessage(),
                            defaultVersionException.getCause());
                }
            }
        } catch (InvalidPolicyClassException e) {
            throw new InvalidPolicyClassException("While getting field "
                    + field + " in " + this + ": " + e.getMessage(), e
                    .getCause());
        }
    }

    public <T> T getSingleReference(Class<T> policyClass)
            throws PolicyGetException, ReferenceNotSetException,
            NoSuchChildPolicyException {
        ContentIdUtil reference = null;

        try {
            reference = getSingleReference();

            if (reference == null) {
                throw new ReferenceNotSetException("Reference  was not set in "
                        + this + ".");
            }

            return PolopolyContext.getPolicy(getCMServer(), reference,
                    policyClass);
        } catch (InvalidPolicyClassException e) {
            throw new InvalidPolicyClassException("While getting reference in "
                    + this + ": " + e.getMessage(), e.getCause());
        } catch (PolicyGetException defaultVersionException) {
            // retry with latest version.
            try {
                return PolopolyContext.getPolicy(getCMServer(), reference
                        .getLatestVersion(), policyClass);
            } catch (PolicyGetException latestException) {
                throw new PolicyGetException("While getting reference in "
                        + this + ": " + defaultVersionException.getMessage(),
                        defaultVersionException.getCause());
            }
        }
    }

    public ContentIdUtil getSingleReference(String field)
            throws PolicyGetException, NoSuchChildPolicyException {
        try {
            ContentId result = getChildPolicy(field, SingleReference.class)
                    .getReference();

            if (result == null) {
                return null;
            } else {
                return util(result, getContext());
            }
        } catch (CMException e) {
            throw new PolicyGetException("Could not get field " + field
                    + " in " + this + ": " + e.getMessage(), e);
        }
    }

    public ContentIdUtil getSingleReference() throws PolicyGetException,
            NoSuchChildPolicyException {
        try {
            ContentId result = CheckedCast.cast(policy, SingleReference.class)
                    .getReference();

            if (result == null) {
                return null;
            } else {
                return util(result, getContext());
            }
        } catch (CMException e) {
            throw new PolicyGetException("Could not get get reference in "
                    + this + ": " + e.getMessage(), e);
        } catch (CheckedClassCastException e) {
            throw new PolicyGetException(this + " is of an unexpected type: "
                    + e.getMessage(), e);
        }
    }

    public ContentListUtil getContentListAware(String field)
            throws NoSuchChildPolicyException {
        try {
            return new ContentListUtilImpl(getChildPolicy(field,
                    ContentListAware.class).getContentList(), this,
                    getContext());
        } catch (CMException e) {
            throw new CMRuntimeException("While getting content list of field "
                    + field + " in " + this + ": " + e.getMessage(), e);
        }
    }

    @Override
    public ContentUtil getContent() {
        if (contentCache == null) {
            contentCache = Util.util(policy.getContent(), getContext());
        }

        return contentCache;
    }

    public Policy asPolicy() {
        return policy;
    }

    public <T> T getChildPolicy(String field, Class<T> klass)
            throws NoSuchChildPolicyException {
        try {
            return CheckedCast.cast(policy.getChildPolicy(field), klass);
        } catch (Exception e) {
            String inputTemplate = getInputTemplate().getExternalIdString();

            throw new NoSuchChildPolicyException("Could not get field " + field
                    + " in " + this + " (a " + inputTemplate + ") : "
                    + e.getMessage(), e);
        }
    }

    public <T> T modify(PolicyModification<T> policyModification, Class<T> klass)
            throws PolicyModificationException {
        return modify(policyModification, klass, true);
    }

    public <T> T modify(PolicyModification<T> policyModification,
            Class<T> klass, boolean createNewVersion)
            throws PolicyModificationException {
        LockInfo lockInfo = policy.getContent().getLockInfo();

        PolicyCMServer server = getCMServer();

        if (createNewVersion) {
            if (lockInfo != null) {
                throw new PolicyModificationException("Content "
                        + policy.getContentId().getContentId()
                                .getContentIdString() + " was locked.");
            }

            try {
                policy = server.createContentVersion(policy.getContentId());
            } catch (CMException e) {
                throw new PolicyModificationException(
                        "While creating new version of "
                                + policy.getContentId().getContentId()
                                        .getContentIdString() + ": "
                                + e.getMessage(), e);
            }
        } else {
            try {
                if (lockInfo == null
                        || !lockInfo.isLockedBy(server.getCurrentCaller())) {
                    throw new PolicyModificationException("Content "
                            + policy.getContentId().getContentId()
                                    .getContentIdString()
                            + " was not locked by current caller.");
                }
            } catch (CMException e) {
                throw new PolicyModificationException("While determining if "
                        + toString() + " was locked: " + e.getMessage(), e);
            }
        }

        try {
            T result = CheckedCast.cast(policy, klass);
            policyModification.modify(result);

            try {
                getContent().commit();
            } catch (CMException e) {
                abort(server, createNewVersion);

                throw new PolicyModificationException("While modifying "
                        + policy.getContentId().getContentId()
                                .getContentIdString() + ": " + e.getMessage(),
                        e);
            }

            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Committed " + this
                        + " after modification.");
            }

            return result;
        } catch (CMException e) {
            abort(server, createNewVersion);

            throw new PolicyModificationException("While modifying "
                    + policy.getContentId().getContentId().getContentIdString()
                    + ": " + e.getMessage(), e);
        } catch (RuntimeException e) {
            abort(server, createNewVersion);

            throw e;
        } catch (CheckedClassCastException e) {
            abort(server, createNewVersion);

            throw new PolicyModificationException("New version of "
                    + policy.getContentId().getContentId().getContentIdString()
                    + ": " + e.getMessage(), e);
        }
    }

    private void abort(PolicyCMServer server, boolean createNewVersion) {
        try {
            server.abortContent(policy, createNewVersion);
        } catch (CMException e1) {
        }
    }

    @SuppressWarnings("unchecked")
    public Iterator<Policy> iterator() {
        try {
            return new FetchingIterator<Policy>() {
                Iterator<String> names = policy.getChildPolicyNames()
                        .iterator();

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
                            logger.log(Level.WARNING,
                                    "While getting child policy "
                                            + childPolicyName + " of " + this
                                            + ": " + e.getMessage(), e);

                            return fetch();
                        }
                    }

                    return null;
                }
            };
        } catch (CMException e) {
            logger.log(Level.WARNING, "While getting child policy names of "
                    + this + ": " + e.getMessage(), e);

            List<Policy> emptyList = Collections.emptyList();
            return emptyList.iterator();
        }
    }

    @Override
    public InputTemplateUtil getInputTemplate() {
        return Util.util(super.getInputTemplate(), getContext());
    }

    @Override
    public String toString() {
        if (getParentPolicy() != null) {
            return childPolicyToString();
        } else {
            return rootPolicyToString();
        }
    }

    protected String rootPolicyToString() {
        String contentIdString = getContent().getExternalIdString();

        if (contentIdString == null) {
            contentIdString = getContentId().getContentId()
                    .getContentIdString();
        }

        String contentName = getContent().getName();

        if (contentName == null || contentName.toString().equals("")) {
            contentName = "unnamed " + getInputTemplate().getExternalIdString();
        }

        return contentName + " (" + contentIdString + ")";
    }

    protected String childPolicyToString() {
        String name;

        try {
            name = policy.getPolicyName();
        } catch (CMException e) {
            name = e.toString();
        }

        return name + " in " + getContentIdString();
    }

    private String getContentIdString() {
        String result = getContent().getExternalIdString();

        if (result == null) {
            result = getContentId().getContentId().getContentIdString();
        }

        return result;
    }

    public void delete() throws PolicyDeleteException {
        String toString = this.toString();

        try {
            getCMServer().removeContent(getContentId().unversioned());

            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Deleted " + toString + ".");
            }
        } catch (CMException e) {
            throw new PolicyDeleteException("While deleting " + toString + ": "
                    + e.getMessage(), e);
        }
    }

    @Override
    public ContentIdUtil getContentId() {
        return Util.util(super.getContentId(), getContext());
    }

    @Override
    public ContentIdUtil getContentReference(String name) {
        try {
            return util(super.getContentReference(name), getContext());
        } catch (CMException e) {
            throw new CMRuntimeException("Could not get reference " + name
                    + " in " + this + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Policy getParentPolicy() {
        return super.getParentPolicy();
    }

    public <T> T getTopPolicy(Class<T> policyClass)
            throws InvalidTopPolicyClassException {
        try {
            return CheckedCast.cast(getTopPolicy(), policyClass);
        } catch (CheckedClassCastException e) {
            throw new InvalidTopPolicyClassException("Top policy of " + this
                    + ": " + e.getMessage());
        }
    }

    public Policy getTopPolicy() {
        Policy result = this;

        try {
            Policy parent;

            while ((parent = result.getParentPolicy()) != null) {
                result = parent;
            }
        } catch (CMException e) {
            throw new CMRuntimeException("While getting parent policy of "
                    + result + ": " + e.getMessage(), e);
        }

        return result;
    }

    public <T> void modifyUtil(
            final PolicyModification<PolicyUtil> policyModification)
            throws PolicyModificationException {
        modify(new PolicyModification<Policy>() {
            public void modify(Policy newVersion) throws CMException {
                policyModification.modify(Util.util(newVersion));
            }
        }, Policy.class);
    }

    public <T> T getSingleReferenceInList(String field, Class<T> policyClass)
            throws PolicyGetException, EmptyListException {
        ContentListUtil contentList = util(this).getContentListAware(field);

        int size = contentList.size();

        if (size > 1) {
            logger.log(Level.WARNING, "There are multiple objects in "
                    + contentList + ".");
        }

        if (size > 0) {
            return contentList.get(0, policyClass);
        }

        throw new EmptyListException();
    }

    public void setSingleReferenceInList(String field, Policy policy) {
        ContentListUtil teaserImageList = util(this).getContentListAware(field);

        teaserImageList.clear();

        if (policy != null) {
            teaserImageList.add(policy);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Policy
                && super.getContentId().equalsIgnoreVersion(
                        ((Policy) obj).getContentId());
    }

    @Override
    public int hashCode() {
        return super.getContentId().getContentId().hashCode();
    }
}

package com.polopoly.util.contentid;

import static com.polopoly.util.policy.Util.util;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.exception.ContentGetException;
import com.polopoly.util.exception.PolicyGetException;
import com.polopoly.util.policy.PolicyUtil;
import com.polopoly.util.policy.Util;

public class ContentIdUtil extends VersionedContentId {
    private transient PolopolyContext context;

    public ContentIdUtil(PolopolyContext context, ContentId contentId) {
        super(
                contentId,
                (contentId instanceof VersionedContentId ? ((VersionedContentId) contentId)
                        .getVersion()
                        : VersionedContentId.UNDEFINED_VERSION));

        if (context == null) {
            throw new IllegalArgumentException("Context was null.");
        }

        this.context = context;
    }

    public PolicyUtil asPolicyUtil() throws PolicyGetException {
        return context.getPolicyUtil(this);
    }

    public Policy asPolicy() throws PolicyGetException {
        return context.getPolicy(this);
    }

    public <T> T asPolicy(Class<T> policyClass) throws PolicyGetException {
        return context.getPolicy(this, policyClass);
    }

    public ContentUtil asContent() throws ContentGetException {
        return context.getContent(this);
    }

    @Override
    public ContentIdUtil getOtherVersionId(int version) {
        return util(super.getOtherVersionId(version), context);
    }

    public ContentIdUtil getDefaultStageVersion() {
        return getOtherVersionId(VersionedContentId.DEFAULT_STAGE_VERSION);
    }

    public ContentIdUtil getLatestCommittedVersion() {
        return getOtherVersionId(VersionedContentId.LATEST_COMMITTED_VERSION);
    }

    public ContentIdUtil getLatestVersion() {
        return getOtherVersionId(VersionedContentId.LATEST_VERSION);
    }

    public ContentIdUtil unversioned() {
        return Util.util(getContentId(), context);
    }

    @Override
    public String getContentIdString() {
        if (getVersion() == UNDEFINED_VERSION) {
            return super.getContentId().getContentIdString();
        } else {
            return super.getContentIdString();
        }
    }

    @Override
    public String toString() {
        if (getVersion() == UNDEFINED_VERSION) {
            return super.getContentId().getContentIdString();
        } else {
            return getContentIdString();
        }
    }

    public ContentIdUtil resolveSymbolicVersion() {
        try {
            return Util.util(context.getPolicyCMServer()
                    .translateSymbolicContentId(this), context);
        } catch (CMException e) {
            throw new CMRuntimeException("While resolving " + this + ": "
                    + e.getMessage(), e);
        }
    }
}

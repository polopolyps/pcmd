package com.polopoly.util.contentid;

import static com.polopoly.util.policy.Util.util;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.policy.Policy;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.exception.ContentGetException;
import com.polopoly.util.exception.PolicyGetException;
import com.polopoly.util.policy.PolicyUtil;

public class ContentIdUtil extends VersionedContentId {
    private PolopolyContext context;

    public ContentIdUtil(PolopolyContext context, ContentId contentId) {
        super(contentId,
                (contentId instanceof VersionedContentId ?
                        ((VersionedContentId) contentId).getVersion() :
                            VersionedContentId.UNDEFINED_VERSION));

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

    public ContentId unversioned() {
        return getContentId();
    }

    @Override
    public String toString() {
        return getContentIdString();
    }
}

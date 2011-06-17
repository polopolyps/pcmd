package com.polopoly.util.client;

import com.polopoly.cm.ContentHistory;
import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentInfo;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.LockInfo;
import com.polopoly.cm.MajorInfo;
import com.polopoly.cm.TagInfo;
import com.polopoly.cm.VersionInfo;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.WorkflowInfo;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.Content;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.client.filter.ContentListFilterChain;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.search.db.SearchExpression;
import com.polopoly.user.server.Caller;

@SuppressWarnings("deprecation")
public class DelegatingPolicyCMServer implements PolicyCMServer {
    private PolicyCMServer delegate;

    public DelegatingPolicyCMServer(PolicyCMServer delegate) {
        this.delegate = delegate;
    }

    public void abortContent(Policy newVersion, boolean removeContentVersion)
            throws CMException {
        delegate.abortContent(newVersion, removeContentVersion);
    }

    public void abortContent(Policy newVersion) throws CMException {
        delegate.abortContent(newVersion);
    }

    public void abortContents(Policy[] newVersions, boolean removeContentVersion)
            throws CMException {
        delegate.abortContents(newVersions, removeContentVersion);
    }

    public void assertLocked(ContentId contentId) throws CMException {
        delegate.assertLocked(contentId);
    }

    public boolean checkPermission(ContentId contentId, String permission,
            boolean checkSessionKey) throws CMException {
        return delegate.checkPermission(contentId, permission, checkSessionKey);
    }

    public void commitContent(Policy newVersion) throws CMException {
        delegate.commitContent(newVersion);
    }

    public void commitContents(Policy[] newVersions) throws CMException {
        delegate.commitContents(newVersions);
    }

    public boolean contentExists(ContentId contentId) throws CMException {
        return delegate.contentExists(contentId);
    }

    public int countContentIdsBySearchExpression(SearchExpression searchExpr)
            throws CMException {
        return delegate.countContentIdsBySearchExpression(searchExpr);
    }

    public Policy createContent(int major, ContentId securityParentId,
            ContentId inputTemplateId) throws CMException {
        return delegate.createContent(major, securityParentId, inputTemplateId);
    }

    public Policy createContent(int major, ContentId inputTemplateId)
            throws CMException {
        return delegate.createContent(major, inputTemplateId);
    }

    public Policy createContent(VersionedContentId newContentId,
            ContentId securityParentId, ContentId inputTemplateId)
            throws CMException {
        return delegate.createContent(newContentId, securityParentId,
                inputTemplateId);
    }

    public Policy createContentCopy(VersionedContentId original,
            ContentId securityParentId) throws CMException {
        return delegate.createContentCopy(original, securityParentId);
    }

    public Policy createContentCopy(VersionedContentId original)
            throws CMException {
        return delegate.createContentCopy(original);
    }

    public Policy createContentVersion(VersionedContentId startVersion,
            ContentId inputTemplateId, boolean setInputTemplateId)
            throws CMException {
        return delegate.createContentVersion(startVersion, inputTemplateId,
                setInputTemplateId);
    }

    public Policy createContentVersion(VersionedContentId startVersion,
            ContentId inputTemplateId) throws CMException {
        return delegate.createContentVersion(startVersion, inputTemplateId);
    }

    public Policy createContentVersion(VersionedContentId startVersion)
            throws CMException {
        return delegate.createContentVersion(startVersion);
    }

    public Policy createEmptyContentVersion(ContentId contentId,
            ContentId inputTemplateId) throws CMException {
        return delegate.createEmptyContentVersion(contentId, inputTemplateId);
    }

    public Policy createUnversionedContent(int major,
            ContentId securityParentId, ContentId inputTemplateId)
            throws CMException {
        return delegate.createUnversionedContent(major, securityParentId,
                inputTemplateId);
    }

    public Policy createUnversionedContent(int major, ContentId inputTemplateId)
            throws CMException {
        return delegate.createUnversionedContent(major, inputTemplateId);
    }

    public VersionedContentId findContentIdByExternalId(
            ExternalContentId externalId) throws CMException {
        return delegate.findContentIdByExternalId(externalId);
    }

    public VersionedContentId[] findContentIdsBySearchExpression(
            SearchExpression searchExpr, int limit, int offset)
            throws CMException {
        return delegate.findContentIdsBySearchExpression(searchExpr, limit,
                offset);
    }

    public VersionedContentId[] findContentIdsBySearchExpression(
            SearchExpression searchExpr, int limit) throws CMException {
        return delegate.findContentIdsBySearchExpression(searchExpr, limit);
    }

    public VersionedContentId[] findContentIdsBySearchExpression(
            SearchExpression searchExpr) throws CMException {
        return delegate.findContentIdsBySearchExpression(searchExpr);
    }

    @SuppressWarnings("deprecation")
    public TagInfo[] getAllTagInfos(ContentId contentId) throws CMException {
        return delegate.getAllTagInfos(contentId);
    }

    public ContentRead getContent(ContentId contentId) throws CMException {
        return delegate.getContent(contentId);
    }

    public ContentHistory getContentHistory(ContentId contentId)
            throws CMException {
        return delegate.getContentHistory(contentId);
    }

    public ContentInfo getContentInfo(ContentId contentId) throws CMException {
        return delegate.getContentInfo(contentId);
    }

    public ContentListFilterChain getContentListFilterChain()
            throws CMException {
        return delegate.getContentListFilterChain();
    }

    @SuppressWarnings("deprecation")
    public ContentRead getContentUnfiltered(ContentId contentId)
            throws CMException {
        return delegate.getContentUnfiltered(contentId);
    }

    public Caller getCurrentCaller() {
        return delegate.getCurrentCaller();
    }

    public MajorInfo[] getHandledMajors() {
        return delegate.getHandledMajors();
    }

    public LockInfo getLockInfo(ContentId contentId) throws CMException {
        return delegate.getLockInfo(contentId);
    }

    public int getMajorByName(String name) throws CMException {
        return delegate.getMajorByName(name);
    }

    public MajorInfo getMajorInfo(int major) throws CMException {
        return delegate.getMajorInfo(major);
    }

    @SuppressWarnings("deprecation")
    public Policy getPolicy(ContentId contentId, ContentId inputTemplateId)
            throws CMException {
        return delegate.getPolicy(contentId, inputTemplateId);
    }

    public Policy getPolicy(ContentId contentId) throws CMException {
        return delegate.getPolicy(contentId);
    }

    @SuppressWarnings("deprecation")
    public Policy getPolicy(ContentId[] contentIds, ContentId inputTemplateId)
            throws CMException {
        return delegate.getPolicy(contentIds, inputTemplateId);
    }

    @SuppressWarnings("deprecation")
    public Policy getPolicy(ContentId[] contentIds) throws CMException {
        return delegate.getPolicy(contentIds);
    }

    @SuppressWarnings("deprecation")
    public Policy getPolicyFor(Content content, ContentId inputTemplateId)
            throws CMException {
        return delegate.getPolicyFor(content, inputTemplateId);
    }

    public Policy getPolicyFor(Content content) throws CMException {
        return delegate.getPolicyFor(content);
    }

    @SuppressWarnings("deprecation")
    public Policy getPolicyFor(Content[] contents, ContentId inputTemplateId)
            throws CMException {
        return delegate.getPolicyFor(contents, inputTemplateId);
    }

    @SuppressWarnings("deprecation")
    public Policy getPolicyFor(Content[] contents) throws CMException {
        return delegate.getPolicyFor(contents);
    }

    public ContentId[] getReferringContentIds(ContentId contentId,
            int versionFlag, String groupName, String name) throws CMException {
        return delegate.getReferringContentIds(contentId, versionFlag,
                groupName, name);
    }

    public ContentId[] getReferringContentIds(ContentId contentId,
            int versionFlag) throws CMException {
        return delegate.getReferringContentIds(contentId, versionFlag);
    }

    public ContentId[] getSecurityChildren(ContentId contentId)
            throws CMException {
        return delegate.getSecurityChildren(contentId);
    }

    public int getTagIdByName(String name) throws CMException {
        return delegate.getTagIdByName(name);
    }

    public String getTagNameById(int tagId) throws CMException {
        return delegate.getTagNameById(tagId);
    }

    public VersionInfo getVersionInfo(VersionedContentId contentId)
            throws CMException {
        return delegate.getVersionInfo(contentId);
    }

    public VersionInfo[] getVersionInfos(ContentId contentId)
            throws CMException {
        return delegate.getVersionInfos(contentId);
    }

    public WorkflowInfo getWorkflowInfo(VersionedContentId contentId)
            throws CMException {
        return delegate.getWorkflowInfo(contentId);
    }

    public void removeContent(ContentId contentId) throws CMException {
        delegate.removeContent(contentId);
    }

    public void setCurrentCaller(Caller caller) {
        delegate.setCurrentCaller(caller);
    }

    public boolean synonymousIds(ContentId oneContentId,
            ContentId anotherContentId) throws CMException {
        return delegate.synonymousIds(oneContentId, anotherContentId);
    }

    public boolean synonymousIdsIgnoreVersion(ContentId oneContentId,
            ContentId anotherContentId) throws CMException {
        return delegate.synonymousIdsIgnoreVersion(oneContentId,
                anotherContentId);
    }

    public VersionedContentId translateSymbolicContentId(ContentId contentId)
            throws CMException {
        return delegate.translateSymbolicContentId(contentId);
    }

    public PolicyCMServer getDelegate() {
        return delegate;
    }

    public LockInfo[] findAllLocks() throws CMException {
        return delegate.findAllLocks();
    }

}

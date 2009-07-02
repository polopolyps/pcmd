package com.polopoly.util.content;

import java.io.IOException;
import java.io.OutputStream;

import com.polopoly.cm.ContentFileInfo;
import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.LockInfo;
import com.polopoly.cm.TagInfo;
import com.polopoly.cm.VersionInfo;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.WorkflowInfo;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.collections.ContentList;
import com.polopoly.user.server.AclId;
import com.polopoly.user.server.UserId;

@SuppressWarnings("deprecation")
public class DelegatingContentRead implements ContentRead {
    protected ContentRead delegate;

    public DelegatingContentRead(ContentRead delegate) {
        this.delegate = delegate;
    }

    protected ContentRead getDelegate() {
        return delegate;
    }

    public boolean checkPermission(String permission, boolean checkSessionKey) {
        return delegate.checkPermission(permission, checkSessionKey);
    }

    public void clearCache() throws CMException {
        delegate.clearCache();
    }

    public void exportFile(String path, OutputStream data) throws CMException, IOException {
        delegate.exportFile(path, data);
    }

    public AclId getAclId() {
        return delegate.getAclId();
    }

    public TagInfo[] getAllTagInfos() {
        return delegate.getAllTagInfos();
    }

    public String[] getAvailableContentListNames() throws CMException {
        return delegate.getAvailableContentListNames();
    }

    public String getComponent(String groupName, String name) throws CMException {
        return delegate.getComponent(groupName, name);
    }

    public String[] getComponentGroupNames() throws CMException {
        return delegate.getComponentGroupNames();
    }

    public String[] getComponentNames(String groupName) throws CMException {
        return delegate.getComponentNames(groupName);
    }

    public long getContentCreationTime() {
        return delegate.getContentCreationTime();
    }

    public VersionedContentId getContentId() {
        return delegate.getContentId();
    }

    public ContentList getContentList() throws CMException {
        return delegate.getContentList();
    }

    public ContentList getContentList(String contentListGroup) throws CMException {
        return delegate.getContentList(contentListGroup);
    }

    public ContentId getContentReference(String groupName, String name) throws CMException {
        return delegate.getContentReference(groupName, name);
    }

    public String[] getContentReferenceGroupNames() throws CMException {
        return delegate.getContentReferenceGroupNames();
    }

    public String[] getContentReferenceNames(String groupName) throws CMException {
        return delegate.getContentReferenceNames(groupName);
    }

    public UserId getCreatedBy() {
        return delegate.getCreatedBy();
    }

    public ExternalContentId getExternalId() throws CMException {
        return delegate.getExternalId();
    }

    public ContentFileInfo getFileInfo(String path) throws CMException, IOException {
        return delegate.getFileInfo(path);
    }

    public ContentId getInputTemplateId() throws CMException {
        return delegate.getInputTemplateId();
    }

    public int getLatestCommittedVersion() {
        return delegate.getLatestCommittedVersion();
    }

    public LockInfo getLockInfo() {
        return delegate.getLockInfo();
    }

    public String getMetaDataComponent(String groupName, String name) throws CMException {
        return delegate.getMetaDataComponent(groupName, name);
    }

    public String[] getMetaDataComponentGroupNames() throws CMException {
        return delegate.getMetaDataComponentGroupNames();
    }

    public String[] getMetaDataComponentNames(String groupName) throws CMException {
        return delegate.getMetaDataComponentNames(groupName);
    }

    public String getName() throws CMException {
        return delegate.getName();
    }

    /**
     * @deprecated
     */
    public ContentId getOutputTemplateId(String mode) throws CMException {
        return delegate.getOutputTemplateId(mode);
    }

    public ContentId[] getReferringContentIds(int versionFlag, String groupName, String name) throws CMException {
        return delegate.getReferringContentIds(versionFlag, groupName, name);
    }

    public ContentId getSecurityParentId() {
        return delegate.getSecurityParentId();
    }

    public VersionInfo getVersionInfo() {
        return delegate.getVersionInfo();
    }

    public WorkflowInfo getWorkflowInfo() {
        return delegate.getWorkflowInfo();
    }

    public boolean isUnversionedContent() {
        return delegate.isUnversionedContent();
    }

    public ContentFileInfo[] listFiles(String dir, boolean recursive) throws CMException, IOException {
        return delegate.listFiles(dir, recursive);
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }


}

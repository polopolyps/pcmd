package com.polopoly.util.content;

import java.io.IOException;
import java.io.InputStream;

import com.polopoly.cm.ComponentDiff;
import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentReferenceDiff;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.Content;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.user.server.AclId;
import com.polopoly.util.CheckedCast;
import com.polopoly.util.CheckedClassCastException;

/**
 * A {@link Content} that delegates all calls to a specified delegate.
 * @author AndreasE
 */
public class DelegatingContent extends DelegatingContentRead implements Content {

    public DelegatingContent(ContentRead delegate) {
        super(delegate);
    }

    private Content getWritableContent() {
        try {
            return CheckedCast.cast(getDelegate(), Content.class);
        } catch (CheckedClassCastException e) {
            throw new CMRuntimeException("The supplied content object for content " + this +
                " was not a Content, but only a ContentRead. It is therefore not writable.");
        }
    }

    public void importFile(String path, InputStream data) throws CMException, IOException {
        getWritableContent().importFile(path, data);
    }

    public void lock() throws CMException {
        getWritableContent().lock();
    }

    public void lock(long expireTimeoutMillis) throws CMException {
        getWritableContent().lock(expireTimeoutMillis);
    }

    @Deprecated
    public void lockMetaData() throws CMException {
        getWritableContent().lockMetaData();
    }

    @Deprecated
    public void lockMetaData(long expireTimeoutMillis) throws CMException {
        getWritableContent().lockMetaData(expireTimeoutMillis);
    }

    public VersionedContentId[] remove() throws CMException {
        return getWritableContent().remove();
    }

    public void removeAcl() throws CMException {
        getWritableContent().removeAcl();
    }

    public void setComponent(String groupName, String name, String value) throws CMException {
        getWritableContent().setComponent(groupName, name, value);
    }

    public void setComponents(ComponentDiff diff) throws CMException {
        getWritableContent().setComponents(diff);
    }

    public void setContentReference(String groupName, String name, ContentId ref) throws CMException {
        getWritableContent().setContentReference(groupName, name, ref);
    }

    public void setContentReferences(ContentReferenceDiff diff) throws CMException {
        getWritableContent().setContentReferences(diff);
    }

    public void setExternalId(String externalId) throws CMException {
        getWritableContent().setExternalId(externalId);
    }

    public void setInputTemplateId(ContentId inputTemplateId) throws CMException {
        getWritableContent().setInputTemplateId(inputTemplateId);
    }

    @Deprecated
    public void setMetaDataComponent(String groupName, String name, String value) throws CMException {
        getWritableContent().setMetaDataComponent(groupName, name, value);
    }

    public void setName(String newName) throws CMException {
        getWritableContent().setName(newName);
    }

    public void setOutputTemplateId(String mode, ContentId outputTemplateId) throws CMException {
        getWritableContent().setOutputTemplateId(mode, outputTemplateId);
    }

    public void setSecurityParentId(ContentId securityParentId) throws CMException {
        getWritableContent().setSecurityParentId(securityParentId);
    }

    public void unlock() throws CMException {
        getWritableContent().unlock();
    }

    public void commit() throws CMException {
        getWritableContent().commit();
    }

    public AclId createAcl() throws CMException {
        return getWritableContent().createAcl();
    }

    public void createDirectory(String path, boolean createParents) throws CMException, IOException {
        getWritableContent().createDirectory(path, createParents);
    }

    public void deleteDirectory(String path, boolean deleteChildren) throws CMException, IOException {
        getWritableContent().deleteDirectory(path, deleteChildren);
    }

    public void deleteFile(String path) throws CMException, IOException {
        getWritableContent().deleteFile(path);
    }

    public void flushCache() throws CMException {
        getWritableContent().flushCache();
    }

    public void forcedUnlock() throws CMException {
        getWritableContent().forcedUnlock();
    }
}

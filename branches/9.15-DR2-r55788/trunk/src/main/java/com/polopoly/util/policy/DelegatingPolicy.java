package com.polopoly.util.policy;

import java.util.List;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.Content;
import com.polopoly.cm.client.InputTemplate;
import com.polopoly.cm.client.OutputTemplate;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.policy.PrepareResult;

public class DelegatingPolicy implements Policy {
    private Policy delegate;

    public DelegatingPolicy(Policy delegate) {
        this.delegate = delegate;
    }

    public Policy getChildPolicy(String name) throws CMException {
        return delegate.getChildPolicy(name);
    }

    @SuppressWarnings("unchecked")
    public List<String> getChildPolicyNames() throws CMException {
        return delegate.getChildPolicyNames();
    }

    public PolicyCMServer getCMServer() throws CMException {
        return delegate.getCMServer();
    }

    public String getComponent(String name) throws CMException {
        return delegate.getComponent(name);
    }

    public String[] getComponentNames() throws CMException {
        return delegate.getComponentNames();
    }

    public Content getContent() {
        return delegate.getContent();
    }

    public VersionedContentId getContentId() {
        return delegate.getContentId();
    }

    public ContentId getContentReference(String name) throws CMException {
        return delegate.getContentReference(name);
    }

    public String[] getContentReferenceNames() throws CMException {
        return delegate.getContentReferenceNames();
    }

    public InputTemplate getInputTemplate() throws CMException {
        return delegate.getInputTemplate();
    }

    public OutputTemplate getOutputTemplate(String mode) throws CMException {
        return delegate.getOutputTemplate(mode);
    }

    public Policy getParentPolicy() throws CMException {
        return delegate.getParentPolicy();
    }

    public String getPolicyName() throws CMException {
        return delegate.getPolicyName();
    }

    public void init(String name, Content[] contents,
            InputTemplate inputTemplate, Policy parentPolicy,
            PolicyCMServer cmServer) {
        delegate.init(name, contents, inputTemplate, parentPolicy, cmServer);
    }

    public void postCommit() throws CMException {
        throw new IllegalStateException("This method should never be called in a delegating policy.");
    }

    public void postCreate() throws CMException {
        throw new IllegalStateException("This method should never be called in a delegating policy.");
    }

    public void postCreateNewVersion() throws CMException {
        throw new IllegalStateException("This method should never be called in a delegating policy.");
    }

    public void preAbort() throws CMException {
        throw new IllegalStateException("This method should never be called in a delegating policy.");
    }

    public void preCommit() throws CMException {
        throw new IllegalStateException("This method should never be called in a delegating policy.");
    }

    public PrepareResult prepare() throws CMException {
        return delegate.prepare();
    }

    public void removePolicyData() throws CMException {
        throw new IllegalStateException("This method should never be called in a delegating policy.");
    }

    public void setComponent(String name, String value) throws CMException {
        delegate.setComponent(name, value);
    }

    public void setContentReference(String name, ContentId ref)
            throws CMException {
        delegate.setContentReference(name, ref);
    }

    public void setPolicyName(String newName) throws CMException {
        delegate.setPolicyName(newName);
    }
}
